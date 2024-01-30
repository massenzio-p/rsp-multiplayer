package org.rsp;

import com.google.common.eventbus.AsyncEventBus;
import org.rsp.registry.CompoundRegistry;
import org.rsp.registry.PlayerQueueRegistry;
import org.rsp.registry.SessionRegistry;

import java.util.concurrent.Executors;

public class ContextHolder {

    private static final int EVENT_BUS_THREADS = 5;
    private static ContextHolder contextHolder;
    private final AsyncEventBus eventBus;
    private final CompoundRegistry compoundRegistry;

    private ContextHolder() {
        this.eventBus = new AsyncEventBus(Executors.newFixedThreadPool(EVENT_BUS_THREADS));
        this.compoundRegistry = new CompoundRegistry();
    }

    public static ContextHolder getInstance() {
        if (contextHolder == null) {
            contextHolder = new ContextHolder();
        }
        return contextHolder;
    }

    public static AsyncEventBus eventBus() {
        return getInstance().eventBus;
    }

    public static SessionRegistry getSessionRegistry() {
        return getInstance().compoundRegistry;
    }

    public static PlayerQueueRegistry getQueueRegistry() {
        return getInstance().compoundRegistry;
    }
}
