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

package net.cdahmedeh.poetwrite.ui.controller;

import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.model.ViewModel;

/**
 * See ./docs/ui-architecture.md for design overview.
 *
 * The ViewController is invoked by the View to perform some business logic.
 *
 * Implementation Guide:
 * - Create methods corresponding to tasks.
 * - These methods should send the business logic on a separate thread by
 *   submitting their code to the TaskBus
 * - Never refer to the ViewModel. Any updated data must come from an AppEvent.
 */
public abstract class ViewController<VM extends ViewModel> {
    protected final VM viewModel;
    protected final TaskBus taskBus;

    protected ViewController(VM viewModel, TaskBus taskBus) {
        this.viewModel = viewModel;
        this.taskBus = taskBus;
    }
}
