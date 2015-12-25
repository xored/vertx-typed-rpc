package com.xored.vertx.typed.rpc;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import io.vertx.ext.unit.TestContext;

/**
 * @author Konstantin Zaitsev
 */
public class TestEventBusServiceImpl implements TestEventBusService {
    private TestContext context;

    public TestEventBusServiceImpl(TestContext context) {
        this.context = context;
    }

    @Override
    public void voidMethod() {
        context.fail();
    }

    @Override
    public void voidMethodStringParams(String param) {
        context.fail();
    }

    @Override
    public void voidMethodBeanParams(TestObject param) {
        context.fail();
    }

    @Override
    public CompletableFuture<String> stringMethod() {
        context.fail();
        return null;
    }

    @Override
    public CompletableFuture<String> stringMethodParams(String param) {
        context.fail();
        return null;
    }

    @Override
    public CompletableFuture<String> stringMethodBeanParams(TestObject param) {
        context.fail();
        return null;
    }

    @Override
    public CompletableFuture<TestObject> beanMethod() {
        context.fail();
        return null;
    }

    @Override
    public CompletableFuture<TestObject> beanMethodStringParams(String param) {
        context.fail();
        return null;
    }

    @Override
    public CompletableFuture<TestObject> beanMethodBeanParams(TestObject param) {
        context.fail();
        return null;
    }

    @Override
    public CompletableFuture<HashMap<String, String>> mapMethodBeanStringParams(TestObject param, String str) {
        context.fail();
        return null;
    }
}
