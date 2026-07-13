package net.cdahmedeh.poetwrite.ui.async;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Used to get the status of the TaskBus. Backend mostly uses it just to know
 * when there's no more task to run, for a clean exit. Really useful for the
 * progress bar in the UI.
 *
 * I'm not actually sure how useful some of these are. The progress numbers
 * were used when we had a progress bar in the UI. But that's gone.
 */
@AllArgsConstructor
@RequiredArgsConstructor
public class TaskBusStatus {
    // The task currently running
    @Getter @Setter
    private AppTask task;

    // How many tasks have been run.
    @Getter @Setter
    private int progress;

    // How many tasks are waiting to be run.
    @Getter @Setter
    private int queued;

    // How many tasks haven't run yet.
    @Getter @Setter
    private int remaining;

    public boolean isBusy() {
        return remaining > 0;
    }

    /**
     * Increment the total number of pending tasks.
     */
    public void queue() {
        this.queued = this.queued + 1;
        this.remaining = this.remaining + 1;
    }

    /**
     * When all tasks are done, call this.
     */
    public void reset() {
        this.progress = 0;
        this.queued = 0;
        this.task = AppTask.empty();
    }

    /**
     * Increments the number of tasks completed.
     */
    public void forward() {
        this.progress = this.progress + 1;
        this.remaining = this.remaining - 1;
    }

    public static TaskBusStatus empty() {
        return new TaskBusStatus(AppTask.empty(), 0, 0, 0);
    }

    public static TaskBusStatus snapshot(TaskBusStatus status) {
        return new TaskBusStatus(
                status.getTask(),
                status.getProgress(),
                status.getQueued(),
                status.getRemaining());
    }
}
