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

public class StatusView extends View<StatusViewModel, StatusViewController, JTabbedPane> {
    private JTabbedPane pane;
    private JButton statusButton;

    public StatusView(StatusViewModel viewModel, StatusViewController viewController) {
        super(viewModel, viewController);
    }

    @Override
    protected void setup() {
        pane = new JTabbedPane();
        statusButton = new JButton("Ready");
        pane.add(statusButton);
    }

    @Override
    public JTabbedPane root() {
        return pane;
    }

    @Override
    protected void subscribe(CompositeDisposable disposable) {
        Disposable busySubscriber = viewController.status()
                .distinctUntilChanged()
                .subscribe(
                        busy -> {
                            System.out.println(busy);
                            statusButton.setText(busy ? "Generating..." : "Ready");
                        }
                );

        disposable.add(busySubscriber);
    }
}
