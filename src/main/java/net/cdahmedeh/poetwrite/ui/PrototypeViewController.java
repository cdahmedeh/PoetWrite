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
import lombok.SneakyThrows;


public class PrototypeViewController extends ViewController<PrototypeViewModel> {
    private final PrototypeTaskHandler taskHandler;

    @AssistedInject
    public PrototypeViewController(@Assisted PrototypeViewModel viewModel, PrototypeTaskHandler taskHandler) {
        super(viewModel);
        this.taskHandler = taskHandler;
    }

    @AssistedFactory
    public interface PrototypeViewControllerFactory {
        PrototypeViewController create(PrototypeViewModel prototypeViewModel);
    }

    public void generateRandomText() {
        taskHandler.submit(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                Thread.sleep(3000);

                String randomText = "This is not really a random string, but we'll pretend that it is.";
                viewModel.setText(randomText);
            }
        });
    }
}
