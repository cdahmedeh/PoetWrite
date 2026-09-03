/**
 * PoetWrite - A Poetry Writing Application
 * Copyright (C) 2026 Ahmed El-Hajjar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.cdahmedeh.poetwrite.query.interfaces;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * One node in the query tree, and one row in the wizard.
 *
 * Everything declared on a step is lazy, so building the tree costs nothing
 * until a column is opened.
 *
 * Nothing here is asynchronous and nothing here knows about the TaskBus.
 * resolve() and render() are blocking; MainViewController is what puts them
 * on the bus and wraps them in an event.
 *
 * TODO: BIG_ONE Each getSteps() rebuilds its children, so navigating back and
 *               forth creates garbage QueryStep instances.
 */
public abstract class QueryStep {

    // TODO: TaskBus still does the job here.

    @Getter
    private final String name;

    protected QueryStep(String name) {
        this.name = name;
    }

    public QueryStep step(String name) {
        return new SimpleQueryStep(name);
    }

    // ------------------------------------------------------------- children

    private Supplier<List<QueryStep>> steps = List::of;
    private boolean stepped = false;

    public QueryStep steps(Supplier<List<QueryStep>> steps) {
        this.steps = steps;
        this.stepped = true;
        return this;
    }

    public List<QueryStep> getSteps() {
        return steps.get();
    }

    public boolean hasSteps() {
        return stepped;
    }

    // ----------------------------------------------------------- parameters

    @Getter
    private final QueryParameters parameters = new QueryParameters();

    public boolean hasParameters() {
        return parameters.has();
    }

    // -------------------------------------------------------------- command

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

    // --------------------------------------------------------------- search

    private Supplier<QuerySearch> search = QuerySearch::new;
    private QuerySearch resolvedSearch = null;
    private boolean searched = false;

    public QueryStep search(Supplier<QuerySearch> search) {
        this.search = search;
        this.searched = true;
        return this;
    }

    /** Resolved once and kept, so the typed text survives between executions. */
    public QuerySearch getSearch() {
        if (resolvedSearch == null) {
            resolvedSearch = search.get();
        }
        return resolvedSearch;
    }

    public boolean hasSearch() {
        return searched;
    }

    // -------------------------------------------------------------- preview

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

    // ------------------------------------------------------------- blocking

    /**
     * This step's column, with the accumulated parameters handed down to it.
     * Blocking: a command may hit a dictionary. Call it from the TaskBus.
     */
    public List<QueryStep> resolve() {
        List<QueryStep> steps = new ArrayList<>(
                hasCommand() ? getCommand().run(this) : getSteps());

        for (QueryStep step : steps) {
            step.getParameters().put(getParameters());
        }

        return steps;
    }

    /**
     * This step's preview text, or null if it has none.
     * Blocking, for the same reason. Call it from the TaskBus.
     */
    public String render() {
        QueryPreview preview = getPreview();
        return preview == null ? null : preview.render(this);
    }
}