package net.cdahmedeh.poetwrite.query.interfaces;

import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import java.util.List;

public class SimpleQueryStep extends QueryStep {
    SimpleQueryStep(String name, TaskBus taskBus) {
        super(name, taskBus);
    }
}
