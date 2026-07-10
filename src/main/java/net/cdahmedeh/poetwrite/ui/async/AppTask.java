package net.cdahmedeh.poetwrite.ui.async;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.cdahmedeh.poetwrite.ui.event.AppEvent;
import net.cdahmedeh.poetwrite.ui.event.TaskBusStartedEvent;

/**
 * See ./docs/async-design.md for design overview.
 *
 * Wrapper for Tasks submitted to TaskBus.
 */
@RequiredArgsConstructor
public class AppTask {
        @Getter
        private final String name;
        @Getter
        private final AppEvent event;
        @Getter
        private final Runnable task;

        /**
         * This is the placeholder task when none are running. Really only for
         * the UI.
         *
         * TODO: Eliminate blank task and have the View display the ready
         *       message.
         */
        public static <E extends AppEvent> AppTask empty() {
            return new AppTask(
                    "",
                    new TaskBusStartedEvent(),
                    () -> {
                    });
        }
}
