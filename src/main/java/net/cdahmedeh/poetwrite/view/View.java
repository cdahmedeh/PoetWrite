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

package net.cdahmedeh.poetwrite.view;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import net.cdahmedeh.poetwrite.controller.ViewController;
import net.cdahmedeh.poetwrite.model.ViewModel;

import java.awt.*;

public abstract class View<VM extends ViewModel, VC extends ViewController, RC extends Component> {
    protected final VM viewModel;
    protected final VC viewController;

    private CompositeDisposable disposable = new CompositeDisposable();

    protected View(VM viewModel, VC viewController) {
        this.viewModel = viewModel;
        this.viewController = viewController;

        setup();
        subscribe(disposable);
    }

    protected abstract void setup();

    public abstract RC root();

    protected abstract void subscribe(CompositeDisposable disposable);
}
