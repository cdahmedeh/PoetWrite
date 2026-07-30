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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The ordered list of options committed so far. This IS the query: each
 * committed option carries an arbitrary {@code value} (a clause object), so
 * a finished chain like
 *
 * <pre>  [Rhyme, Target.NEXT_LINE, Means("darkness"), Candidate("fortress")]</pre>
 *
 * can be handed to whatever executes/inserts the result. The wizard itself
 * never interprets values — it only accumulates them.
 *
 * Immutable; steps receive the chain-so-far when producing their options,
 * so later steps can depend on earlier choices.
 */
public final class WizardChain {

    public static final WizardChain EMPTY = new WizardChain(List.of());

    private final List<WizardOption> selections;

    private WizardChain(List<WizardOption> selections) {
        this.selections = selections;
    }

    WizardChain plus(WizardOption option) {
        List<WizardOption> next = new ArrayList<>(selections);
        next.add(option);
        return new WizardChain(Collections.unmodifiableList(next));
    }

    public List<WizardOption> selections() {
        return selections;
    }

    /** The clause values of every committed option, in order. */
    public List<Object> values() {
        return selections.stream().map(WizardOption::value).toList();
    }

    public int size() {
        return selections.size();
    }

    public boolean isEmpty() {
        return selections.isEmpty();
    }

    /** First clause value assignable to {@code type}, or null. Convenience for step factories. */
    @SuppressWarnings("unchecked")
    public <T> T find(Class<T> type) {
        for (WizardOption o : selections) {
            if (type.isInstance(o.value())) {
                return (T) o.value();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return values().toString();
    }
}