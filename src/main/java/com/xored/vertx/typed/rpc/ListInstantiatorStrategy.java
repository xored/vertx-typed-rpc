package com.xored.vertx.typed.rpc;

import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.BaseInstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Konstantin Zaitsev
 */
class ListInstantiatorStrategy extends BaseInstantiatorStrategy {
    private final StdInstantiatorStrategy delegate = new StdInstantiatorStrategy();

    @SuppressWarnings("rawtypes")
    private static class ListInstantiator implements ObjectInstantiator<List> {
        @Override
        public List newInstance() {
            return new ArrayList();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public ObjectInstantiator newInstantiatorOf(Class type) {
        if (type.isInterface() && List.class.isAssignableFrom(type)) {
            return new ListInstantiator();
        }
        return delegate.newInstantiatorOf(type);
    }
}
