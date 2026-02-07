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

package net.cdahmedeh.poetwrite.ui;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

public class StatusViewController extends ViewController<StatusViewModel> {
    @AssistedInject
    protected StatusViewController(@Assisted StatusViewModel viewModel, AsynchronousTaskHandler taskHandler) {
        super(viewModel, taskHandler);

        listen();
    }

    @AssistedFactory
    public interface StatusViewControllerFactory {
        StatusViewController create(StatusViewModel statusViewModel);
    }

    public void listen() {
        taskHandler.stream()
                .subscribe(busy -> {
                    viewModel.setRunningTasksCount(taskHandler.count());
                    try {
                        viewModel.setCurrentTaskName(taskHandler.current().getName());
                    } catch (Exception e) {
//                        e.printStackTrace();
                    }
                    viewModel.setTasksHandlerBusy(busy);
                    viewModel.setLeftTasksCount(taskHandler.left());
                });
    }
}
