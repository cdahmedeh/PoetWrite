package net.cdahmedeh.poetwrite.query.interfaces;

/**
 * The detail shown beside the wizard while a step is highlighted.
 *
 * Runs on the TaskBus via QueryStep.preview(), so it may be slow -- walking
 * dictionaries is fine here. Takes the step, so it can call methods on the
 * QueryStep subclass that declared it.
 */
public class QueryPreview {
    public String render(QueryStep step) {
        return null;
    }
}