package net.cdahmedeh.poetwrite.query.interfaces;

import lombok.Getter;
import net.cdahmedeh.poetwrite.query.event.QueryStepExecutedEvent;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.event.async.ServiceStartingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * TODO: BIG_ONE: The only reason this works is because of the TaskBus single
 *                threadpool. So the initializing is called first. And the
 *                guarantee of the execution is because Dagger calls the
 *                constructor as soon as it's injected. Therefore, any calls
 *                to a LazyService method is done after running the constructor
 *                and therefore the init code.
 * TODO: BIG_ONE Each time this is run, new QueryStep is created. So we'll have
 *               a ton of garbage QueryStep.
 */
public abstract class QueryStep {
    private volatile boolean initialized = false;

    @Getter
    private final String name;

    protected final TaskBus taskBus;

    protected QueryStep(String name, TaskBus taskBus) {
        this.name = name;
        this.taskBus = taskBus;

//        taskBus.stream().subscribe(status -> {
//            listen(status.getTask(), status.getTask().getEvent());
//        });

//        ensure();
    }

    public void ensure() {
        if (initialized) return;

        taskBus.submit(String.format("Starting %s",getName()), new ServiceStartingEvent(), () -> {
//            init();
            initialized = true;
        });
    };

//    protected abstract void init();

//    public abstract List<QueryStep> execute();

    public QueryStep step(String name) {
        return new SimpleQueryStep(name, taskBus);
    }

    protected <T extends QueryStep> T step(Function<TaskBus, T> constructor) {
        return constructor.apply(taskBus);
    }

    private Supplier<List<QueryStep>> steps = List::of;
    private boolean stepped = false;
    public QueryStep steps(Supplier<List<QueryStep>> steps) {
        this.steps = steps;
        stepped = true;
        return this;
    }
    public List<QueryStep> getSteps() {
        return steps.get();
    }
    public boolean hasSteps() {
        return stepped;
    }

    private final QueryParameters parameters = new QueryParameters();
    public QueryParameters getParameters() {
        return parameters;
    }
    public boolean hasParameters() {
        return parameters.has();
    }

    private QueryCommand command = null;
    private boolean commanded = false;
    public QueryStep command(QueryCommand command) {
        this.command = command;
        this.commanded = true;
        return this;
    }
    public QueryCommand getCommand() {
        return command;
    }
    public boolean hasCommand() {
        return commanded;
    }

    private Supplier<QueryPreview> preview = () -> null;
    private boolean previewed = false;
    public QueryStep preview(Supplier<QueryPreview> preview) {
        this.preview = preview;
        this.previewed = true;
        return this;
    }
    public QueryPreview getPreview() {
        return preview.get();
    }
    public boolean hasPreview() {
        return previewed;
    }

    private Supplier<QuerySearch> search = () -> null;
    private boolean searched = false;
    public QueryStep search(Supplier<QuerySearch> search) {
        this.search = search;
        this.searched = true;
        return this;
    }
    public QuerySearch getSearch() {
        return search.get();
    }
    public boolean hasSearch() {
        return searched;
    }

    public void execute() {
        QueryStepExecutedEvent event = new QueryStepExecutedEvent(this);

        taskBus.submit("Query: " + getName(), event, new Runnable() {
            @Override
            public void run() {
                List<QueryStep> steps = new ArrayList<>();

                if (hasCommand()) {
                    steps.addAll(getCommand().run(getParameters()));
                } else {
                    steps.addAll(getSteps());
                }

                for (QueryStep step : steps) {
                    // Kind of weird
                    step.getParameters().put(getParameters());
                }

                event.getSteps().addAll(steps);
            }
        });
    }

    // TODO: Claude suggested this
//    public void execute() {
//        QueryStepExecutedEvent event = new QueryStepExecutedEvent(this);
//
//        taskBus.submit("Query: " + getName(), event, () -> {
//            List<QueryStep> next = hasCommand() ? getCommand().run(getParameters()) : getSteps();
//            next.forEach(step -> step.getParameters().putAll(getParameters()));
//            event.setSteps(next);
//        });
//    }
}
