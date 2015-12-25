package com.xored.vertx.typed.rpc;

import io.vertx.core.eventbus.EventBus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use {@link EventBus#publish(String, Object)} for broadcast message. The method should be one way
 * with returns void otherwise annotation will ignored.
 * 
 * @author Konstantin Zaitsev
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Publish {
}
