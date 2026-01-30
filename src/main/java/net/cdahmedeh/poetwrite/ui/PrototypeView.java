/**
 * PoetWrite - A Poetry Writing Application
 * Copyright (C) 2025 Ahmed El-Hajjar
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

import io.reactivex.rxjava3.disposables.Disposable;

import javax.swing.*;
import java.awt.*;

public class PrototypeView {

    private final PrototypeViewModel viewModel;
    private final PrototypeViewController viewController;

    private Disposable textSubscriber;
    private Disposable busySubscriber;

    private final JFrame frame;
    private final JTextField textAreaField;
    private final JButton generateRandomTextButton;

    public PrototypeView(PrototypeViewModel viewModel, PrototypeViewController viewController) {
        this.viewModel = viewModel;
        this.viewController = viewController;

        frame = new JFrame("PoetWrite");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        frame.setLayout(new BorderLayout());

        textAreaField = new JTextField();
        frame.add(textAreaField, BorderLayout.CENTER);

        generateRandomTextButton = new JButton("Generate Random Text");
        frame.add(generateRandomTextButton, BorderLayout.SOUTH);

        generateRandomTextButton.addActionListener(e -> viewController.generateRandomText());

        textSubscriber = viewModel.streamText()
                .distinctUntilChanged()
                .subscribe(newText -> {
                    SwingUtilities.invokeLater(() -> textAreaField.setText(newText));
                });

        busySubscriber = viewModel.streamBusy()
                .distinctUntilChanged()
                .subscribe(isBusy -> {
                    SwingUtilities.invokeLater(() -> {
                        generateRandomTextButton.setText(isBusy ? "Generating..." : "Generate Random Text");
                    });
                }
                );
    }

    public void show() {
        SwingUtilities.invokeLater(() -> frame.show());
    }
}
