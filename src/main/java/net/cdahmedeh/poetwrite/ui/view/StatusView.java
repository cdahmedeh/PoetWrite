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

import com.formdev.flatlaf.extras.FlatSVGIcon;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import net.cdahmedeh.poetwrite.ui.component.SpinningIcon;
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
                "[grow, fill] [235!] [20!]",
                "[min!]"));

        currentTaskNameLabel = new JLabel(UIConstants.STRING_STATUS_DEFAULT);
        currentTaskNameLabel.setFont(currentTaskNameLabel.getFont().deriveFont(Font.PLAIN));
        currentTaskNameLabel.setForeground(new Color(155, 155, 155));
        pane.add(currentTaskNameLabel, "cell 1 0, w 235!, aligny center");

        taskActivityStatusIcon = new JLabel();
        taskActivityStatusIcon.setPreferredSize(new Dimension(16, 16));
        taskActivityStatusIcon.setHorizontalAlignment(SwingConstants.CENTER);
        pane.add(taskActivityStatusIcon, "cell 2 0, w 20!, aligny center");
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

    private final Timer ellipsisTimer = new Timer(350, null); // 500ms per dot
    private int dotCount = 0;
    private String currentBaseText = "";

    {
        ellipsisTimer.addActionListener(e -> {
            dotCount = (dotCount % 3) + 1; // cycles 1 -> 2 -> 3 -> 1...
            currentTaskNameLabel.setText(currentBaseText + ".".repeat(dotCount));
        });
    }

    @Override
    protected void subscribe(CompositeDisposable disposable) {
        FlatSVGIcon stoppedIcon = new FlatSVGIcon(getClass().getResource("/icons/done.svg"));
        stoppedIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color ->  new Color(225, 225, 225)));
        FlatSVGIcon baseIcon = new FlatSVGIcon("icons/busy.svg", 16, 16);
        baseIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> new Color(200, 200, 200)));
        SpinningIcon spinnerIcon = new SpinningIcon(baseIcon, taskActivityStatusIcon);

        Disposable taskSubscriber = viewModel.stream().subscribe(status -> {

            SwingUtilities.invokeLater(() -> {
                if (!status.isBusy()) {
                    ellipsisTimer.stop();
//                    currentTaskNameLabel.setText(UIConstants.STRING_STATUS_DEFAULT + "...");
                    currentTaskNameLabel.setText("");
                    taskActivityStatusIcon.setIcon(stoppedIcon);
                } else {
                    taskActivityStatusIcon.setIcon(spinnerIcon);
                    spinnerIcon.start();

                    currentBaseText = status.getTask().getName();
                    dotCount = 0;
                    currentTaskNameLabel.setText(currentBaseText);
                    ellipsisTimer.start();
                }
            });
            });

        disposable.add(taskSubscriber);
    }
}
