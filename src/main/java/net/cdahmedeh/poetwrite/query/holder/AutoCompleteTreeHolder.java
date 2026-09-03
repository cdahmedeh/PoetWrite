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

package net.cdahmedeh.poetwrite.query.holder;

import net.cdahmedeh.poetwrite.query.interfaces.QueryStep;
import net.cdahmedeh.poetwrite.query.steps.RootQueryStep;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Owns the autocomplete tree so the ViewController does not have to.
 *
 * Building walks every step class and can block, so tree() is expected to be
 * called from the TaskBus -- see MainViewController.requestAutoComplete().
 *
 * TODO: The cached tree keeps the parameters accumulated on the root and on
 *       RhymeWithQueryStep from the previous invocation. Deeper levels are
 *       rebuilt by getSteps() each time so they start clean, but these two do
 *       not. Either rebuild per request, or clear the parameters here, once
 *       it is clear which the wizard wants.
 *
 *  TODO: tree() hangs on the first time, and then re-used. Should look into
 *        using the cache that is used accross the board.
 */
@Singleton
public class AutoCompleteTreeHolder {

    private final RootQueryStep rootQueryStep;

    private QueryStep tree = null;

    @Inject
    public AutoCompleteTreeHolder(RootQueryStep rootQueryStep) {
        this.rootQueryStep = rootQueryStep;
    }

    public QueryStep tree() {
        if (tree == null) {
            tree = rootQueryStep.build();
        }
        return tree;
    }
}