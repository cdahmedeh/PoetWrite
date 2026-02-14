package net.cdahmedeh.poetwrite.ui.async;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@RequiredArgsConstructor
public class TaskBusStatus {
    @Getter @Setter
    private AsyncTask current;
    @Getter @Setter
    private boolean busy;
    @Getter @Setter
    private int progress;
    @Getter @Setter
    private int total;

    public static TaskBusStatus empty() {
        return new TaskBusStatus(AsyncTask.empty(), false, 0, 0);
    }
}
