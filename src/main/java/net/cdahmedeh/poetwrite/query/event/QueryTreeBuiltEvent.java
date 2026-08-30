package net.cdahmedeh.poetwrite.query.event;

import lombok.Getter;
import lombok.Setter;
import net.cdahmedeh.poetwrite.query.interfaces.QueryStep;
import net.cdahmedeh.poetwrite.ui.event.interfaces.AppEvent;

/**
 * The autocomplete tree is ready. Separate from
 * AutoCompleteWizardRequestedEvent so the popup can be shown immediately and
 * filled in when the build finishes.
 */
public class QueryTreeBuiltEvent extends AppEvent {
    @Getter @Setter
    private QueryStep root;
}