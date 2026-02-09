package net.cdahmedeh.poetwrite.generator;

import net.cdahmedeh.poetwrite.async.AsynchronousTaskHandler;
import net.cdahmedeh.poetwrite.event.ServiceStartingEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class LazyService {
    private volatile boolean initialized = false;
    private final AtomicBoolean queued = new AtomicBoolean(false);

    protected final AsynchronousTaskHandler taskHandler;

    protected LazyService(AsynchronousTaskHandler taskHandler) {
        this.taskHandler = taskHandler;
        ensure();
    }

    public abstract String name();

    public void ensure() {
        if (initialized) return;
        if (!queued.compareAndSet(false, true)) return;

        taskHandler.submit(String.format("Starting %s ",name()), new ServiceStartingEvent(), () -> {
            init();
            initialized = true;
        });
    };

    protected abstract void init();
}
