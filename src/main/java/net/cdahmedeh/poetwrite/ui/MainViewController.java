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

import java.util.Random;


public class MainViewController extends ViewController<MainViewModel> {
    @AssistedInject
    public MainViewController(@Assisted MainViewModel viewModel, AsynchronousTaskHandler taskHandler) {
        super(viewModel, taskHandler);
    }

    @AssistedFactory
    public interface MainViewControllerFactory {
        MainViewController create(MainViewModel mainViewModel);
    }

    public void generateRandomText() {
        taskHandler.submit("Generating Random Text " + new Random().nextDouble(), new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                Thread.sleep(3000);

                Double randomNumber = new Random().nextDouble();
                String randomText = randomNumber.toString();
                viewModel.setText(randomText);
            }
        });
    }
}
