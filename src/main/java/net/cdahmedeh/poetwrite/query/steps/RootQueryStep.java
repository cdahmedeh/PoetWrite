package net.cdahmedeh.poetwrite.query.steps;

import net.cdahmedeh.poetwrite.query.interfaces.QueryStep;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class RootQueryStep extends QueryStep {

    @Inject
    public RootQueryStep() {
        super("root");
    }

    /**
     * Builds the tree. Blocking -- AutoCompleteTreeHolder calls this from
     * the TaskBus.
     */
    public QueryStep build() {
        return steps(() -> List.of(new RhymeWithQueryStep().steps()));
    }
}
