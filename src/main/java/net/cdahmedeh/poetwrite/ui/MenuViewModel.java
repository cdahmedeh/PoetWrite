package net.cdahmedeh.poetwrite.ui;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class MenuViewModel extends ViewModel {
    private BehaviorSubject<AsynchronousTaskHandler.AsynchronousTaskHandlerStatus> taskHandlerStatus = BehaviorSubject.createDefault(AsynchronousTaskHandler.AsynchronousTaskHandlerStatus.empty());

    public void setTasksHandlerStatus(AsynchronousTaskHandler.AsynchronousTaskHandlerStatus status) {
        taskHandlerStatus.onNext(status);
    }

    public Observable<AsynchronousTaskHandler.AsynchronousTaskHandlerStatus> streamTasksHandlerStatus() {
        return taskHandlerStatus.hide();
    }
}
