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

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import lombok.SneakyThrows;
import net.cdahmedeh.poetwrite.generator.TextGenerator;

import java.util.Random;


public class MainViewController extends ViewController<MainViewModel> {
    private final TextGenerator textGenerator;

    @AssistedInject
    public MainViewController(@Assisted MainViewModel viewModel, AsynchronousTaskHandler taskHandler, TextGenerator textGenerator) {
        super(viewModel, taskHandler);
        this.textGenerator = textGenerator;
    }

    @AssistedFactory
    public interface MainViewControllerFactory {
        MainViewController create(MainViewModel mainViewModel);
    }

    public void generateRandomText() {
        for (int i = 0; i < 10; i++) {
            taskHandler.submit("Generating Random Text " + new Random().nextDouble(), new TextUpdateEvent(), () -> {
                String text = textGenerator.generate();
                viewModel.setText(text);
            });
        }
    }
}
