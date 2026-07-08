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

package net.cdahmedeh.poetwrite.ui.view;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import net.cdahmedeh.poetwrite.ui.constant.UIConstants;
import net.cdahmedeh.poetwrite.ui.controller.StatusViewController;
import net.cdahmedeh.poetwrite.ui.model.StatusViewModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: This is a huge mess. For whoever is reading this, please don't judge
 *       me.
 * TODO: Hate that Swing doesn't have some kind of Markup. Even WPF has XAML.
 */
public class StatusView extends View<StatusViewModel, StatusViewController, JPanel> {
    private JPanel pane;

    private JLabel currentTaskNameLabel;
    private JProgressBar tasksRunningProgressBar;
    private JLabel taskActivityStatusIcon;

    public StatusView(StatusViewModel viewModel, StatusViewController viewController) {
        super(viewModel, viewController);
    }

    @Override
    protected void setup() {
        pane = new JPanel(new BorderLayout());

        setupProgressBar();
    }

    private void setupProgressBar() {

        pane.setLayout(new MigLayout(
                "insets dialog",
                "[grow, fill] [20!] [200!] [120!]",
                "[min!]"));

        currentTaskNameLabel = new JLabel(UIConstants.STRING_STATUS_DEFAULT);
        currentTaskNameLabel.setFont(currentTaskNameLabel.getFont().deriveFont(Font.BOLD));
        currentTaskNameLabel.setForeground(Color.GRAY);
        pane.add(currentTaskNameLabel, "cell 2 0, w 200!, aligny center");

        tasksRunningProgressBar = new JProgressBar();
        pane.add(tasksRunningProgressBar, "cell 3 0, w 120!, aligny center");
        tasksRunningProgressBar.setStringPainted(true);
        tasksRunningProgressBar.setFont(tasksRunningProgressBar.getFont().deriveFont(13f));
        tasksRunningProgressBar.setForeground(Color.GRAY);

        taskActivityStatusIcon = new JLabel();
        taskActivityStatusIcon.setPreferredSize(new Dimension(16, 16));
        taskActivityStatusIcon.setHorizontalAlignment(SwingConstants.CENTER);
        pane.add(taskActivityStatusIcon, "cell 1 0, w 20!, aligny center");
    }

    @Override
    protected void listen() {

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
        ImageIcon stoppedIcon = new ImageIcon(getClass().getResource("/icons/stopped.png"));
        ImageIcon spinnerIcon = new ImageIcon(getClass().getResource("/icons/spinner.gif"));

        Disposable taskSubscriber = viewModel.stream().subscribe(status -> {

        SwingUtilities.invokeLater(() -> {
            tasksRunningProgressBar.setStringPainted(true);
                    tasksRunningProgressBar.setString(String.format("%d/%d", status.getProgress(), status.getQueued()));
                    if (status.isBusy() == false) {
                        tasksRunningProgressBar.setString(UIConstants.STRING_STATUS_COMPLETE);
                        currentTaskNameLabel.setText(UIConstants.STRING_STATUS_DEFAULT + "...");
                        tasksRunningProgressBar.setMaximum(10);
                        tasksRunningProgressBar.setValue(10);
                        taskActivityStatusIcon.setIcon(stoppedIcon);
                    } else {
                        taskActivityStatusIcon.setIcon(spinnerIcon);
                        currentTaskNameLabel.setText(status.getTask().getName());
                        tasksRunningProgressBar.setMaximum(status.getQueued());
                        tasksRunningProgressBar.setValue(status.getProgress());
                    }
                });
            });

        disposable.add(taskSubscriber);
    }
}
