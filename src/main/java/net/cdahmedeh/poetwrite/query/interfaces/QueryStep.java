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
 * This is the most important of the auto-complete handling system.
 *
 * At the base of everything is a QueryStep, an entry in the auto-complete
 * wizard. You can see it as a node of a tree if you want.
 *
 * QueryStep defines the following things:
 * - The name of the step itself.
 * - The steps inside the step. Keep in mind, this is not something, this is
 *   when something is hard-coded in.
 * - It contains a command, which is a dynamic system that will feel in the
 *   next steps. For example, based on a user selection or a search query.
 * - It can contain a search query. Text that the user entered and that will
 *   determine the next step. Such as the results of a dictionary search.
 * - A preview. A tooltip that shows up when this step is currently highlighted.
 *   This could for example, be the pattern group that is selected, or
 *   the definition of a highlighted word.
 *
 *  Very important, as the user steps through, parameters are stored based on
 *  user selections. As previous selections can affect the query. These
 *  parameters are collected as the user makes their selection.
 *
 *  Absolutely everything is declarated in such a way that is lazy. When
 *  feeding, for example, a lookup command, it is not called until the wizard
 *  arrives at that step. This is to avoid
 *
 * Everything declared on a step is lazy, so building the tree costs nothing
 * until a column is opened. So a costly query is not run until we actually
 * get to that step. This is a performance decision, each command should do the
 * bare minimum to show its sub-steps.
 *
 * VERY IMPORTANT: Nothing here is actually asynchronous. Everything is
 *                 BLOCKING. Just like everything we've done so far, it is the
 *                 responsibility of the caller to put the computations in the
 *                 task bus and wait for the event. Maybe once I get clever,
 *                 find someway to have the TaskBus used automatically. But
 *                 could you imagine the confusion?
 *
 * EVEN MORE IMPORTANT: To allow steps to be computed or built in a way that is
 *                      dynamic, so we don't force the whole tree to be built
 *                      as soon as the auto-complete dialogue is requested.
 *                      So we're using lambda to do this. For all the methods,
 *                      that related to building or previewing the tree, you
 *                      need to pass a supplier for a query or compute method.
 *
 * TODO: BIG_ONE Each getSteps() rebuilds its children, so navigating back and
 *               forth creates garbage QueryStep instances.
 *
 * TODO: Dependencies are loaded thanks to the constructor being called on the
 *       TaskBus, almost accidental by design. The win is that we don't need
 *       a lazy loading method like we did with LazyService. BUT, this also
 *       means we have no control over the loading flow. BIG MISTAKE.
 */
public abstract class QueryStep {

    @Getter
    private final String name;

    // This part is called as soon as the dependency is injected. Because it's
    // wrapped in TaskBus normally, this already happens before any other of the
    // other methods are called. Not happy with this.
    protected QueryStep(String name) {
        this.name = name;
    }

    public QueryStep step(String name) {
        return new SimpleQueryStep(name);
    }

    // Hard-coded steps. -------------------------------------------------------

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

    // Parameters that are accumulated as the query is built. ------------------

    @Getter
    private final QueryParameters parameters = new QueryParameters();

    public boolean hasParameters() {
        return parameters.has();
    }

    // This is like steps() but dynamic based on the result of a computation.
    // -------------------------------------------------------------------------

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

    // A user entered text query. Such as a dictionary lookup.
    // -------------------------------------------------------------------------

    private Supplier<QuerySearch> search = QuerySearch::new;
    private QuerySearch resolvedSearch = null;
    private boolean searched = false;

    public QueryStep search(Supplier<QuerySearch> search) {
        this.search = search;
        this.searched = true;
        return this;
    }

    // Resolved search is my messed up way to keep the existing search query
    // inside the step.
    // TODO: Redesign search.
    public QuerySearch getSearch() {
        if (resolvedSearch == null) {
            resolvedSearch = search.get();
        }
        return resolvedSearch;
    }

    public boolean hasSearch() {
        return searched;
    }

    // What is displayed if the query step has some kind of preview. Like a
    // definition of a word.
    // -------------------------------------------------------------------------

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

    // This is the key part where the steps are actually computed based on the
    // lambda supplier.
    // This is not called by the QueryStep. The UI, through the controller,
    // will call this to start building the tree.
    // WRAP IT IN A TASKBUS.
    public List<QueryStep> resolve() {
        List<QueryStep> steps = new ArrayList<>(
                hasCommand() ? getCommand().run(this) : getSteps());

        for (QueryStep step : steps) {
            step.getParameters().put(getParameters());
        }

        return steps;
    }

    // The other visiual part, showing the preview of a step. Again, a
    // controller calls this.
    // WRAP IT IN A TASKBUS
    public String render() {
        QueryPreview preview = getPreview();
        return preview == null ? null : preview.render(this);
    }
}