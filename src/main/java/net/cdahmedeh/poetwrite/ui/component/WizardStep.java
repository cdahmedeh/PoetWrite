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

package net.cdahmedeh.poetwrite.ui.component;

import net.cdahmedeh.poetwrite.ui.async.AppTask;
import net.cdahmedeh.poetwrite.ui.event.interfaces.AppEvent;

import java.util.List;
import java.util.function.Consumer;

/**
 * One stage of the wizard: given everything committed so far and whatever the
 * user has typed into this pane's text field (empty string if the pane has no
 * field, or nothing typed yet), produce the options to show.
 *
 * {@code options} is re-invoked on every keystroke in the pane's field, so
 * filtering strategy belongs to the step, not the component.
 *
 * Most steps are fast (in-memory lists, cheap filtering) and only need to
 * implement {@link #options}. A step doing slow work — a dictionary lookup,
 * a TaskBus round-trip — should instead override {@link #optionsAsync} and do
 * the work off the EDT, calling the callback exactly once when done, from any
 * thread. The host marshals the result back to the EDT and silently drops it
 * if a newer keystroke has already superseded the request, so a step never
 * needs to worry about cancellation or ordering itself.
 */
public interface WizardStep {

    List<WizardOption> options(WizardChain chainSoFar, String typed);

    /**
     * Async variant. The default just calls {@link #options} and hands the
     * result straight back, so synchronous steps get this for free — only
     * override it for steps that need to do slow work off the EDT.
     */
    default void optionsAsync(WizardChain chainSoFar, String typed,
                              Consumer<List<WizardOption>> callback) {
        callback.accept(options(chainSoFar, typed));
    }

    /** True if this pane shows an editable text field above the list. */
    default boolean hasTextField() {
        return false;
    }

    /** Placeholder text for the field (rendered by FlatLaf). */
    default String placeholder() {
        return "";
    }
}