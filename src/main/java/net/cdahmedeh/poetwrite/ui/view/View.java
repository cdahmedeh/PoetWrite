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
import net.cdahmedeh.poetwrite.ui.controller.ViewController;
import net.cdahmedeh.poetwrite.ui.model.ViewModel;

import java.awt.*;

/**
 * See ./docs/ui-architecture.md for design overview.
 *
 * The View is what draws the screens and allows the user to interact with the
 * application.
 *
 * Implementation Guide:
 * - Implement setup() for laying out your UI.
 * - Make root() return the top of the component hierarchy. Usually a JFrame.
 * - Implement subscribe() to subscribe to the observables in the model. You
 *   can listen to them for changes, and update the View accordingly.
 * - Implement listeners so you can add, normally actions listeners, to the
 *   various UI items. This isn't enforced, you should just create the UI
 *   first in setup(), and then setup the listeners in listen().
 *
 * - You can call the ViewController upon user interaction to run some logic.
 * - You can call the ViewModel to subscribe to the observables.
 */
public abstract class View<VM extends ViewModel, VC extends ViewController, RC extends Component> {
    protected final VM viewModel;
    protected final VC viewController;

    private CompositeDisposable disposable = new CompositeDisposable();

    protected View(VM viewModel, VC viewController) {
        this.viewModel = viewModel;
        this.viewController = viewController;

        setup();
        subscribe(disposable);
        listen();
    }

    protected abstract void setup();

    protected abstract void listen();

    public abstract RC root();

    protected abstract void subscribe(CompositeDisposable disposable);
}
