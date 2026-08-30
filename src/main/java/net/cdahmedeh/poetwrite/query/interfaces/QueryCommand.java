package net.cdahmedeh.poetwrite.query.interfaces;

import java.util.List;

/**
 * Produces the next column. Runs on the TaskBus; QueryStep.execute() owns that.
 *
 * Takes the step rather than the parameters so a command can reach everything
 * it belongs to: the accumulated parameters, and the QuerySearch holding
 * whatever the user typed.
 */
@FunctionalInterface
public interface QueryCommand {
    List<QueryStep> run(QueryStep step);
}