package com.xored.vertx.typed.rpc;

import static com.xored.vertx.typed.rpc.EventBusServiceFactory.createClient;
import static com.xored.vertx.typed.rpc.EventBusServiceFactory.registerServer;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * @author Konstantin Zaitsev
 */
@SuppressWarnings("Duplicates")
@RunWith(VertxUnitRunner.class)
public class EventBusServiceTest {
    private Vertx vertx;
    private TestEventBusService client;

    @Before
    public void setUp(@SuppressWarnings("UnusedParameters") TestContext context) {
        vertx = Vertx.vertx();
        client = createClient(vertx.eventBus(), TestEventBusService.class);

    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testVoidMethod(TestContext context) {
        final Async asyncServer = context.async();
        registerServer(vertx.eventBus(), new TestEventBusServiceImpl(context) {
            @Override
            public void voidMethod() {
                asyncServer.complete();
                context.assertTrue(true);
            }
        });

        client.voidMethod();
    }

    @Test
    public void testVoidMethodStringParams(TestContext context) {
        final Async asyncServer = context.async();
        registerServer(vertx.eventBus(), new TestEventBusServiceImpl(context) {
            @Override
            public void voidMethodStringParams(String param) {
                asyncServer.complete();
                context.assertEquals("test1", param);
            }
        });

        client.voidMethodStringParams("test1");
    }

    @Test
    public void testVoidMethodBeanParams(TestContext context) {
        final Async asyncServer = context.async();
        registerServer(vertx.eventBus(), new TestEventBusServiceImpl(context) {
            @Override
            public void voidMethodBeanParams(TestObject param) {
                context.assertEquals("test1", param.getStr());
                context.assertEquals(123, param.getNum());
                context.assertEquals(10.0, param.getP().getX());
                context.assertEquals(20.0, param.getP().getY());
                asyncServer.complete();
            }
        });

        client.voidMethodBeanParams(new TestObject("test1", 123, 10, 20));
    }

    @Test
    public void testStringMethod(TestContext context) {
        final Async asyncServer = context.async();
        final Async asyncClient = context.async();
        registerServer(vertx.eventBus(), new TestEventBusServiceImpl(context) {
            @Override
            public CompletableFuture<String> stringMethod() {
                asyncServer.complete();
                return CompletableFuture.completedFuture("test1");
            }
        });

        client.stringMethod().thenAccept(s -> {
            context.assertEquals("test1", s);
            asyncClient.complete();
        });
    }

    @Test
    public void testStringMethodParams(TestContext context) {
        final Async asyncServer = context.async();
        final Async asyncClient = context.async();
        registerServer(vertx.eventBus(), new TestEventBusServiceImpl(context) {
            @Override
            public CompletableFuture<String> stringMethodParams(String param) {
                context.assertEquals("test1param", param);
                asyncServer.complete();
                return CompletableFuture.completedFuture("test1");
            }
        });

        client.stringMethodParams("test1param").thenAccept(s -> {
            context.assertEquals("test1", s);
            asyncClient.complete();
        });
    }

    @Test
    public void testStringMethodBeanParams(TestContext context) {
        final Async asyncServer = context.async();
        final Async asyncClient = context.async();
        registerServer(vertx.eventBus(), new TestEventBusServiceImpl(context) {
            @Override
            public CompletableFuture<String> stringMethodBeanParams(TestObject param) {
                context.assertEquals("test1", param.getStr());
                context.assertEquals(123, param.getNum());
                context.assertEquals(10.0, param.getP().getX());
                context.assertEquals(20.0, param.getP().getY());
                asyncServer.complete();
                return CompletableFuture.completedFuture("test1");
            }
        });

        client.stringMethodBeanParams(new TestObject("test1", 123, 10, 20)).thenAccept(s -> {
            context.assertEquals("test1", s);
            asyncClient.complete();
        });
    }

    @Test
    public void testBeanMethod(TestContext context) {
        final Async asyncServer = context.async();
        final Async asyncClient = context.async();
        registerServer(vertx.eventBus(), new TestEventBusServiceImpl(context) {
            @Override
            public CompletableFuture<TestObject> beanMethod() {
                asyncServer.complete();
                return CompletableFuture.completedFuture(new TestObject("test1", 123, 10, 20));
            }
        });

        client.beanMethod().thenAccept(s -> {
            context.assertEquals("test1", s.getStr());
            context.assertEquals(123, s.getNum());
            context.assertEquals(10.0, s.getP().getX());
            context.assertEquals(20.0, s.getP().getY());
            asyncClient.complete();
        });
    }

    @Test
    public void testBeanMethodStringParams(TestContext context) {
        final Async asyncServer = context.async();
        final Async asyncClient = context.async();
        registerServer(vertx.eventBus(), new TestEventBusServiceImpl(context) {
            @Override
            public CompletableFuture<TestObject> beanMethodStringParams(String param) {
                context.assertEquals("test1param", param);
                asyncServer.complete();
                return CompletableFuture.completedFuture(new TestObject("test1", 123, 10, 20));
            }
        });

        client.beanMethodStringParams("test1param").thenAccept(s -> {
            context.assertEquals("test1", s.getStr());
            context.assertEquals(123, s.getNum());
            context.assertEquals(10.0, s.getP().getX());
            context.assertEquals(20.0, s.getP().getY());
            asyncClient.complete();
        });
    }

    @Test
    public void testBeanMethodBeanParams(TestContext context) {
        final Async asyncServer = context.async();
        final Async asyncClient = context.async();
        registerServer(vertx.eventBus(), new TestEventBusServiceImpl(context) {
            @Override
            public CompletableFuture<TestObject> beanMethodBeanParams(TestObject param) {
                asyncServer.complete();
                context.assertEquals("test1param", param.getStr());
                context.assertEquals(1234, param.getNum());
                context.assertEquals(11.0, param.getP().getX());
                context.assertEquals(22.0, param.getP().getY());
                return CompletableFuture.completedFuture(new TestObject("test1", 123, 10, 20));
            }
        });

        client.beanMethodBeanParams(new TestObject("test1param", 1234, 11, 22)).thenAccept(s -> {
            context.assertEquals("test1", s.getStr());
            context.assertEquals(123, s.getNum());
            context.assertEquals(10.0, s.getP().getX());
            context.assertEquals(20.0, s.getP().getY());
            asyncClient.complete();
        });
    }

    @Test
    public void testMapMethodBeanStringParams(TestContext context) {
        final Async asyncServer = context.async();
        final Async asyncClient = context.async();
        registerServer(vertx.eventBus(), new TestEventBusServiceImpl(context) {
            @Override
            public CompletableFuture<HashMap<String, String>> mapMethodBeanStringParams(TestObject param, String str) {
                asyncServer.complete();
                context.assertEquals("test1str", str);
                context.assertEquals("test1param", param.getStr());
                context.assertEquals(1234, param.getNum());
                context.assertEquals(11.0, param.getP().getX());
                context.assertEquals(22.0, param.getP().getY());
                HashMap<String, String> map = new HashMap<>();
                map.put("test1", "test2");
                return CompletableFuture.completedFuture(map);
            }
        });

        client.mapMethodBeanStringParams(new TestObject("test1param", 1234, 11, 22), "test1str").thenAccept(s -> {
            context.assertEquals(1, s.size());
            context.assertTrue(s.containsKey("test1"));
            context.assertEquals("test2", s.get("test1"));
            asyncClient.complete();
        });
    }

    @SuppressWarnings({"ConstantConditions", "InstanceofInterfaces", "Duplicates"})
    @Test
    public void testStringMethodException(TestContext context) {
        final Async asyncServer = context.async();
        final Async asyncClient = context.async();
        registerServer(vertx.eventBus(), new TestEventBusServiceImpl(context) {
            @Override
            public CompletableFuture<String> stringMethod() {
                asyncServer.complete();
                CompletableFuture<String> result = new CompletableFuture<>();
                result.completeExceptionally(new TestException((short) 10, "test"));
                return result;
            }
        });

        TestEventBusService client = createClient(vertx.eventBus(), TestEventBusService.class);

        client.stringMethod().thenAccept(s -> context.fail()).exceptionally(ex -> {
            context.assertNotNull(ex);
            context.assertTrue(ex.getCause() instanceof TestException);
            TestException gameException = (TestException) ex.getCause();
            context.assertEquals(10, gameException.getErrorCode());
            context.assertEquals("test", gameException.getMessage());
            asyncClient.complete();
            return null;
        });
    }

    @SuppressWarnings({"ConstantConditions", "InstanceofInterfaces", "Duplicates"})
    @Test
    public void testStringMethodRuntimeException(TestContext context) {
        final Async asyncServer = context.async();
        final Async asyncClient = context.async();
        registerServer(vertx.eventBus(), new TestEventBusServiceImpl(context) {
            @Override
            public CompletableFuture<String> stringMethod() {
                asyncServer.complete();
                throw new TestException((short) 10, "test");
            }
        });

        TestEventBusService client = createClient(vertx.eventBus(), TestEventBusService.class);

        client.stringMethod().thenAccept(s -> context.fail()).exceptionally(ex -> {
            context.assertNotNull(ex);
            context.assertTrue(ex.getCause() instanceof TestException);
            TestException exception = (TestException) ex.getCause();
            context.assertEquals(10, exception.getErrorCode());
            context.assertEquals("test", exception.getMessage());
            asyncClient.complete();
            return null;
        });
    }

    @Test
    public void testStringNullMethodParams(TestContext context) {
        final Async asyncServer = context.async();
        final Async asyncClient = context.async();
        registerServer(vertx.eventBus(), new TestEventBusServiceImpl(context) {
            @Override
            public CompletableFuture<String> stringMethodParams(String param) {
                context.assertEquals("test1param", param);
                asyncServer.complete();
                return CompletableFuture.<String> completedFuture(null);
            }
        });

        client.stringMethodParams("test1param").thenAccept(s -> {
            context.assertNull(s);
            asyncClient.complete();
        });
    }

    @Test
    public void testStringMethodParamsNull(TestContext context) {
        final Async asyncServer = context.async();
        final Async asyncClient = context.async();
        registerServer(vertx.eventBus(), new TestEventBusServiceImpl(context) {
            @Override
            public CompletableFuture<String> stringMethodParams(String param) {
                context.assertNull(param);
                asyncServer.complete();
                return CompletableFuture.completedFuture("test1");
            }
        });

        client.stringMethodParams(null).thenAccept(s -> {
            context.assertEquals("test1", s);
            asyncClient.complete();
        });
    }

}