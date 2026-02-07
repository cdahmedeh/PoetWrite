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

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class StatusView extends View<StatusViewModel, StatusViewController, JPanel> {
    private JPanel pane;
    private JButton nameButton;
    private JButton numberButton;
    private JButton leftButton;

    public StatusView(StatusViewModel viewModel, StatusViewController viewController) {
        super(viewModel, viewController);
    }

    @Override
    protected void setup() {
        pane = new JPanel(new BorderLayout());
        nameButton = new JButton("Ready");
        pane.add(nameButton, BorderLayout.WEST);

        numberButton = new JButton("0");
        pane.add(numberButton, BorderLayout.CENTER);

        leftButton = new JButton("0");
        pane.add(leftButton, BorderLayout.EAST);
    }

    @Override
    public JPanel root() {
        return pane;
    }

    @Override
    protected void subscribe(CompositeDisposable disposable) {
        Disposable busySubscriber = viewModel.streamTasksHandlerBusy()
                .subscribe(busy -> {
                    System.out.println("Status View: Busy: " + busy);
                });

        disposable.add(busySubscriber);

        Disposable taskNameSubscriber = viewModel.streamCurrentTaskName()
                .subscribe(name -> {
                    nameButton.setText(name);
                });

        disposable.add(taskNameSubscriber);

        Disposable taskCountSubscriber = viewModel.streamRunningTasksCount()
                .subscribe(count -> {
                    numberButton.setText(String.valueOf(count));
                });

        disposable.add(taskCountSubscriber);

        Disposable leftCountSubscriber = viewModel.streamLeftTasksCount()
                .subscribe(count -> {
                    leftButton.setText(String.valueOf(count));
                });
    }
}
