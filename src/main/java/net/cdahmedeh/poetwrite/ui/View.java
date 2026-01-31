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

public abstract class View<VM extends ViewModel, VC extends ViewController> {
    protected final VM viewModel;
    protected final VC viewController;

    protected CompositeDisposable disposable = new CompositeDisposable();

    protected View(VM viewModel, VC viewController) {
        this.viewModel = viewModel;
        this.viewController = viewController;
    }
}
