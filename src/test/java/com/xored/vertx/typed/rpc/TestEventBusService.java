package com.xored.vertx.typed.rpc;

import com.xored.vertx.typed.rpc.EventBusService;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * @author Konstantin Zaitsev
 */
@SuppressWarnings("SameParameterValue")
@EventBusService("test")
public interface TestEventBusService {
    void voidMethod();

    void voidMethodStringParams(String param);

    void voidMethodBeanParams(TestObject param);

    CompletableFuture<String> stringMethod();

    CompletableFuture<String> stringMethodParams(String param);

    CompletableFuture<String> stringMethodBeanParams(TestObject param);

    CompletableFuture<TestObject> beanMethod();

    CompletableFuture<TestObject> beanMethodStringParams(String param);

    CompletableFuture<TestObject> beanMethodBeanParams(TestObject param);

    CompletableFuture<HashMap<String, String>> mapMethodBeanStringParams(TestObject param, String str);
}
