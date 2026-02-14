package net.cdahmedeh.poetwrite.ui.async;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.cdahmedeh.poetwrite.ui.event.AppEvent;
import net.cdahmedeh.poetwrite.ui.event.TaskBusStartedEvent;

@RequiredArgsConstructor
public class AsyncTask {
        @Getter
        private final String name;
        @Getter
        private final AppEvent event;
        @Getter
        private final Runnable task;

        public static <E extends AppEvent> AsyncTask empty() {
            return new AsyncTask(
                    "Task Handler Starting",
                    new TaskBusStartedEvent(),
                    () -> {
                    });
        }
}
