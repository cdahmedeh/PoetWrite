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

import com.formdev.flatlaf.extras.FlatSVGIcon;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class StatusView extends View<StatusViewModel, StatusViewController, JPanel> {
    private JPanel pane;
    private JLabel nameButton;
    private JButton numberButton;
    private JButton leftButton;
    private JProgressBar progressBar;
    private JLabel spinner;

    public StatusView(StatusViewModel viewModel, StatusViewController viewController) {
        super(viewModel, viewController);
    }

    @Override
    protected void setup() {
        pane = new JPanel(new BorderLayout());
//        pane.setLayout(new MigLayout("insets dialog", "[fill]"));

        pane.setLayout(new MigLayout(
                "insets dialog",
                "[grow, fill] [30!] [200!] [120!]",
                "[bottom] []"));

        JSeparator seperator = new JSeparator();
        pane.add(seperator, "cell 0 0 4 1");

        ImageIcon spinnerIcon = new ImageIcon(getClass().getResource("/icons/spinner.gif"));
        ImageIcon stoppedIcon = new ImageIcon(getClass().getResource("/icons/stopped.png"));

        nameButton = new JLabel("Ready");
        pane.add(nameButton, "cell 2 1, w 200!");

        progressBar = new JProgressBar();
        pane.add(progressBar, "cell 3 1, w 120!");
        progressBar.setStringPainted(true);
        progressBar.setFont(progressBar.getFont().deriveFont(13f));

        spinner = new JLabel();
        spinner.setPreferredSize(new Dimension(16, 16));
        spinner.setHorizontalAlignment(SwingConstants.CENTER);
        pane.add(spinner, "cell 1 1, w 30!");
    }

    @Override
    public JPanel root() {
        return pane;
    }

    int left = 0;
    int current = 0;
    String name = "";

    @Override
    protected void subscribe(CompositeDisposable disposable) {
        Disposable busySubscriber = viewModel.streamTasksHandlerBusy()
                .subscribe(busy -> {
                    progressBar.setStringPainted(true);
                    progressBar.setString(String.format("%d/%d", left - current, left));
                    if (busy == false) {
                        progressBar.setString("done");
                        nameButton.setText("Ready...");
                        progressBar.setMaximum(1);
                        progressBar.setValue(1);
                        ImageIcon stoppedIcon = new ImageIcon(getClass().getResource("/icons/stopped.png"));
                        spinner.setIcon(stoppedIcon);
                    } else {
                        ImageIcon spinnerIcon = new ImageIcon(getClass().getResource("/icons/spinner.gif"));
                        spinner.setIcon(spinnerIcon);
                    }
                });

        disposable.add(busySubscriber);

        Disposable currentTaskNameSubscriber = viewModel.streamCurrentTaskName()
                .subscribe(name -> {
                    nameButton.setText(name);
                    this.name = name;
                });

        disposable.add(currentTaskNameSubscriber);

        Disposable currentTaskCountSubscriber = viewModel.streamRunningTasksCount()
                .subscribe(count -> {
                    progressBar.setValue(left - count);
                    current = count;
                });

        disposable.add(currentTaskCountSubscriber);

        Disposable leftTaskCountSubscriber = viewModel.streamLeftTasksCount()
                .subscribe(count -> {
                    left = count;
                    progressBar.setMaximum(count);
                    if (count == 0) {progressBar.setMaximum(1); progressBar.setValue(1);}
                });

        disposable.add(leftTaskCountSubscriber);
    }
}
