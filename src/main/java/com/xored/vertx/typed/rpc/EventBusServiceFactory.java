package com.xored.vertx.typed.rpc;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Factory to create client and server service for RPC communication.
 * 
 * @author Konstantin Zaitsev
 */
@Slf4j
public class EventBusServiceFactory {
    private static final String HEADER_METHOD_NAME = "method";

    private static final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
        @SuppressWarnings("rawtypes")
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.register(Map.class, new MapSerializer() {
                protected Map create(Kryo kryo, Input input, java.lang.Class<Map> type) {
                    return new HashMap();
                }
            });
            kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new ListInstantiatorStrategy()));
            return kryo;
        }
    };

    /**
     * Creates proxy client that invokes appropriate method of RPC service.
     * 
     * @param eventBus EventBus instance
     * @param iface    RPC service interface that marked with {@link EventBusService} annotation.
     * 
     * @return proxy client of interface that use EventBus for communication. 
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public static <T> T createClient(EventBus eventBus, Class<T> iface) {
        EventBusService service = getEventBusServiceInterface(iface).getAnnotation(EventBusService.class);
        if (service == null) {
            throw new RuntimeException("Interface should has EventBusService annotiation.");
        }

        String address = service.value();

        @SuppressWarnings("unchecked")
        T instance = (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class[] { iface },
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    Class<?>[] classes = method.getParameterTypes();
                    DeliveryOptions options = new DeliveryOptions().addHeader(HEADER_METHOD_NAME, methodName);
                    Buffer buffer = null;

                    if (classes.length > 0) {
                        buffer = writeObjects(args);
                    }
                    if (method.getReturnType() == void.class) {
                        if (method.getAnnotation(Publish.class) != null) {
                            eventBus.publish(address, buffer, options);
                        } else {
                            eventBus.send(address, buffer, options);
                        }
                        return null;
                    } else {
                        Class<?> returnType = method.getReturnType();
                        if (returnType.isAssignableFrom(CompletableFuture.class)) {
                            CompletableFuture<Object> result = new CompletableFuture<>();
                            eventBus.<Buffer>send(address, buffer, options, r -> {
                                if (r.failed()) {
                                    result.completeExceptionally(r.cause());
                                    return;
                                }
                                Message<Buffer> msg = r.result();

                                if (msg != null) {
                                    Buffer buf = msg.body();
                                    Registration clazz = kryos.get().readClass(new Input(buf.getBytes()));

                                    if (clazz != null && Throwable.class.isAssignableFrom(clazz.getType())) {
                                        result.completeExceptionally(
                                                (Throwable) kryos.get().readClassAndObject(new Input(buf.getBytes())));
                                    } else {
                                        result.complete(readObject(msg.body()));
                                    }
                                } else {
                                    result.complete(null);
                                }
                            });
                            return result;
                        }
                        throw new RuntimeException("EventBusService support only CompletableFuture returns");
                    }
                });
        return instance;
    }

    /**
     * Registers RPC service in Vertx EventBus.
     * 
     * @param eventBus      EventBus instance
     * @param serverHandler RPC service implementation
     *  
     * @return EventBus message consumer that can be used to unregister service.
     */
    public static <T> MessageConsumer<Buffer> registerServer(EventBus eventBus, T serverHandler) {
        log.debug("Register EventBus Service: {}", serverHandler.getClass().getName());
        HashMap<String, Method> methods = new HashMap<>();

        Class<?> serviceInterface = getEventBusServiceInterface(serverHandler.getClass());
        EventBusService serviceAnnotation = serviceInterface.getAnnotation(EventBusService.class);
        String address = serviceAnnotation.value();
        for (Method method : serviceInterface.getDeclaredMethods()) {
            method.setAccessible(true);
            methods.put(method.getName(), method);
        }
        MessageConsumer<Buffer> consumer = eventBus.<Buffer>consumer(address);
        consumer.handler(r -> {
            try {
                String methodName = r.headers().get(HEADER_METHOD_NAME);
                if (!methods.containsKey(methodName)) {
                    String msg = String.format("Method %s not found", methodName);
                    log.error(msg);
                    r.fail(1, msg);
                    return;
                }

                Method method = methods.get(methodName);
                Object result = null;

                try {
                    if (method.getParameterTypes().length == 0) {
                        result = method.invoke(serverHandler);
                    } else {
                        Object[] objects = readObjects(r.body(), method.getParameterTypes().length);
                        result = method.invoke(serverHandler, objects);
                    }

                    if (method.getReturnType().isAssignableFrom(CompletableFuture.class)) {
                        ((CompletableFuture<?>) result).whenComplete((msg, e) -> {
                            if (e != null) {
                                r.reply(writeObject(e));
                            } else {
                                r.reply(writeObject(msg));
                            }
                        });
                    }
                } catch (InvocationTargetException ex) {
                    r.reply(writeObject(ex.getTargetException()));
                }
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
                r.fail(-1, e.getMessage());
            }
        });
        return consumer;
    }

    private static Class<?> getEventBusServiceInterface(Class<?> clazz) {
        if (clazz.getAnnotation(EventBusService.class) != null) {
            return clazz;
        }
        for (Class<?> iface : clazz.getInterfaces()) {
            if (iface.getAnnotation(EventBusService.class) != null) {
                return iface;
            }
        }
        if (clazz.getSuperclass() != null) {
            return getEventBusServiceInterface(clazz.getSuperclass());
        }
        throw new RuntimeException(String.format("%s interface has not EventBusService annotation", clazz.getName()));
    }

    private static Object[] readObjects(Buffer buffer, int count) {
        final Input input = new Input(buffer.getBytes());
        if (count == 1) {
            return new Object[] { kryos.get().readClassAndObject(input) };
        }

        List<Object> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(kryos.get().readClassAndObject(input));
        }
        return result.toArray(new Object[result.size()]);
    }

    @SuppressWarnings("unchecked")
    private static <T> T readObject(Buffer buffer) {
        return (T) kryos.get().readClassAndObject(new Input(buffer.getBytes()));
    }

    private static Buffer writeObjects(Object[] objs) {
        final Output output = new Output(2048, Integer.MAX_VALUE);
        for (int i = 0; i < objs.length; i++) {
            kryos.get().writeClassAndObject(output, objs[i]);
        }
        return Buffer.buffer(output.toBytes());
    }

    private static Buffer writeObject(Object objs) {
        final Output output = new Output(2048, Integer.MAX_VALUE);
        kryos.get().writeClassAndObject(output, objs);
        return Buffer.buffer(output.toBytes());
    }
}
