package com.xored.vertx.typed.rpc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Wrap interface as RPC service over Vertx EventBus.
 *  
 * @author Konstantin Zaitsev
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventBusService {
    /**
     * Event bus address
     */
    String value();
}
