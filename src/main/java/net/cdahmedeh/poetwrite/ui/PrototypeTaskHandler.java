package net.cdahmedeh.poetwrite.ui;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class PrototypeTaskHandler {
    private BehaviorSubject<AtomicInteger> count = BehaviorSubject.createDefault(new AtomicInteger(0));

    private final ExecutorService pool = Executors.newSingleThreadExecutor();

    private final AtomicInteger taskCount = new AtomicInteger(0);

    public void runTask(Runnable task) {
        taskCount.incrementAndGet();

        pool.submit(() -> {
            try {
                task.run();
            } finally {
                taskCount.decrementAndGet();
            }
        });
    }

    public boolean isBusy() {
        return taskCount.get() > 0;
    }
}
