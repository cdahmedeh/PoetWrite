package net.cdahmedeh.poetwrite.ui.async;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
public class TaskBusStatus {
    @Getter @Setter
    private AppTask task;
    @Getter @Setter
    private int progress;
    @Getter @Setter
    private int queued;
    @Getter @Setter
    private int remaining;

    public boolean isBusy() {
        return remaining > 0;
    }

    public void queue() {
        this.queued = this.queued + 1;
        this.remaining = this.remaining + 1;
    }

    public void reset() {
        this.progress = 0;
        this.queued = 0;
        this.task = AppTask.empty();
    }

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
