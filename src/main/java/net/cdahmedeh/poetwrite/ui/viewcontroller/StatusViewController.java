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

package net.cdahmedeh.poetwrite.ui.viewcontroller;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.viewmodel.StatusViewModel;

public class StatusViewController extends ViewController<StatusViewModel> {
    @AssistedInject
    protected StatusViewController(@Assisted StatusViewModel viewModel, TaskBus taskBus) {
        super(viewModel, taskBus);

        listen();
    }

    @AssistedFactory
    public interface StatusViewControllerFactory {
        StatusViewController create(StatusViewModel statusViewModel);
    }

    public void listen() {
        // Wait for notifications from the TaskBus and pass them on to the
        // Model as an event.
        //
        // TODO: Like I mentioned in StatusViewModel, I'm not that happy that
        //       the listening is done in here. But at the same time, I don't
        //       want the model to have access to the TaskBus.
        taskBus.monitor()
                .subscribe(status -> {
                    viewModel.setTaskHandlerStatus(status);
                });
    }
}
