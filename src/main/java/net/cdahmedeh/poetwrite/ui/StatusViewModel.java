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

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import static java.lang.String.format;

public class StatusViewModel extends ViewModel {
    private BehaviorSubject<Boolean> tasksHandlerBusy = BehaviorSubject.createDefault(false);
    private BehaviorSubject<String> currentTaskName = BehaviorSubject.createDefault("Ready...");
    private BehaviorSubject<Integer> runningTasksCount = BehaviorSubject.createDefault(0);
    private BehaviorSubject<Integer> leftTasksCount = BehaviorSubject.createDefault(0);

    public void setTasksHandlerBusy(boolean busy) {
        this.tasksHandlerBusy.onNext(busy);
    }

    public void setCurrentTaskName(String currentTaskName) {
        this.currentTaskName.onNext(format("%s...", currentTaskName));
    }

    public void setRunningTasksCount(Integer runningTasksCount) {
        this.runningTasksCount.onNext(runningTasksCount);
    }

    public void setLeftTasksCount(Integer leftTasksCount) {
        this.leftTasksCount.onNext(leftTasksCount);
    }

    public Observable<Boolean> streamTasksHandlerBusy() {
        return this.tasksHandlerBusy.hide();
    }

    public Observable<String> streamCurrentTaskName() {
        return this.currentTaskName.hide();
    }

    public Observable<Integer> streamRunningTasksCount() {
        return this.runningTasksCount.hide();
    }

    public Observable<Integer> streamLeftTasksCount() {
        return this.leftTasksCount.hide();
    }
}
