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

import lombok.SneakyThrows;

public class PrototypeViewController extends ViewController<PrototypeViewModel> {
    public PrototypeViewController(PrototypeViewModel viewModel) {
        super(viewModel);
    }

    public void generateRandomText() {
        new Thread(new Runnable() {
            @Override
            @SneakyThrows
            public void run() {
                viewModel.setBusy(true);

                Thread.sleep(5000);

                String randomText = "This is not really a random string, but we'll pretend that it is.";

                System.out.println(randomText);

                viewModel.setText(randomText);

                viewModel.setBusy(false);
            }
        }).start();
    }
}
