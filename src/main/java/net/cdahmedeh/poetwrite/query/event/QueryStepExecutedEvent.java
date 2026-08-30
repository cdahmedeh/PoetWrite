package net.cdahmedeh.poetwrite.query.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.cdahmedeh.poetwrite.query.interfaces.QueryStep;
import net.cdahmedeh.poetwrite.ui.event.interfaces.AppEvent;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class QueryStepExecutedEvent extends AppEvent {
    @Getter
    private final QueryStep step;

    @Getter
    private final List<QueryStep> steps = new ArrayList<>();
}
