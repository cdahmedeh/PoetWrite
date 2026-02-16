package net.cdahmedeh.poetwrite.ui.async;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.cdahmedeh.poetwrite.ui.event.AppEvent;
import net.cdahmedeh.poetwrite.ui.event.TaskBusStartedEvent;

@RequiredArgsConstructor
public class AppTask {
        @Getter
        private final String name;
        @Getter
        private final AppEvent event;
        @Getter
        private final Runnable task;

        public static <E extends AppEvent> AppTask empty() {
            return new AppTask(
                    "Ready",
                    new TaskBusStartedEvent(),
                    () -> {
                    });
        }
}
