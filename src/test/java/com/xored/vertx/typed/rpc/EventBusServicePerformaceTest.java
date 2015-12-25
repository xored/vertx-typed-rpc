package com.xored.vertx.typed.rpc;

import static com.xored.vertx.typed.rpc.EventBusServiceFactory.createClient;
import static com.xored.vertx.typed.rpc.EventBusServiceFactory.registerServer;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Konstantin Zaitsev
 */
@SuppressWarnings("Duplicates")
@RunWith(VertxUnitRunner.class)
public class EventBusServicePerformaceTest {
    private static final long ITERATION_COUNT = 100000L;
    private Vertx vertx;
    private TestEventBusService client;
    private static final Map<String, Long> times = new HashMap<>();

    @Before
    public void setUp(@SuppressWarnings("UnusedParameters") TestContext context) {
        vertx = Vertx.vertx();
        client = createClient(vertx.eventBus(), TestEventBusService.class);

    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @AfterClass
    public static void summary() {
        System.out.println("Execution of " + ITERATION_COUNT + " req/rep operation in different modes:");
        for (Entry<String, Long> time : times.entrySet()) {
            System.out.println("  " + time.getKey() + " - time: " + time.getValue() + "ms, "
                    + ITERATION_COUNT * 1000L / time.getValue() + " op/s");
        }
    }
    
    @Test
    public void performanceEventBusTest(TestContext context) throws InterruptedException {
        long time = System.currentTimeMillis();
        vertx.eventBus().<JsonObject>consumer("test1", m -> {
            m.reply(new JsonObject().put("str", "string").put("num", 123).put("p",
                    new JsonObject().put("x", 1).put("y", 2)));
        });
        AtomicLong count = new AtomicLong(0);
        for (int i = 0; i < ITERATION_COUNT; i++) {
            vertx.eventBus().send("test1", new JsonObject().put("str", "string1").put("num", 321).put("p",
                    new JsonObject().put("x", 3).put("y", 4)), r -> count.incrementAndGet());
        }
        waitAndReport("EventBus", count, time);
    }

    @Test
    public void performanceRPCTest(TestContext context) throws InterruptedException {
        long time = System.currentTimeMillis();
        registerServer(vertx.eventBus(), new TestEventBusServiceImpl(context) {
            @Override
            public CompletableFuture<TestObject> beanMethodBeanParams(TestObject param) {
                return CompletableFuture.completedFuture(new TestObject("string", 123, 1, 2));
            }
        });
        AtomicLong count = new AtomicLong(0);
        for (int i = 0; i < ITERATION_COUNT; i++) {
            client.beanMethodBeanParams(new TestObject("string1", 321, 3, 4))
                    .whenComplete((m, e) -> count.incrementAndGet());
        }

        waitAndReport("RPC", count, time);
    }

    private void waitAndReport(String name, AtomicLong count, long time) {
        while (count.get() < ITERATION_COUNT) {
        }
        times.put(name, (System.currentTimeMillis() - time));
    }
}