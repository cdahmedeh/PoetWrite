package net.cdahmedeh.poetwrite.query.steps;

import lombok.Getter;
import net.cdahmedeh.poetwrite.query.interfaces.QueryStep;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class RootQueryStep extends QueryStep {
    @Inject
    public RootQueryStep(TaskBus taskBus) {
        super("root", taskBus);
    }

    public QueryStep build() {
        return steps(
                () -> List.of(step(RhymeWithQueryStep::new).steps())
        );
    }

}
