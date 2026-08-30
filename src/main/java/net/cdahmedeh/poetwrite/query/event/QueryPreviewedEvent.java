package net.cdahmedeh.poetwrite.query.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.cdahmedeh.poetwrite.query.interfaces.QueryStep;
import net.cdahmedeh.poetwrite.ui.event.interfaces.AppEvent;

/**
 * A step's preview text is ready. Carries the step so the wizard can tell
 * whether the highlight has moved on since it asked.
 */
@RequiredArgsConstructor
public class QueryPreviewedEvent extends AppEvent {
    @Getter
    private final QueryStep step;

    @Getter @Setter
    private String text;
}