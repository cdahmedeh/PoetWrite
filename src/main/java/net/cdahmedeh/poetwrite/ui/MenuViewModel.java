package net.cdahmedeh.poetwrite.ui;

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Singleton;

public class MenuViewModel extends ViewModel {
    @AssistedInject
    public MenuViewModel(AsynchronousTaskHandler taskHandler) {
        super(taskHandler);
    }

    @AssistedFactory
    public interface MenuViewModelFactory {
        MenuViewModel create();
    }

    @Override
    protected void listen(AsynchronousTaskHandler.AsynchronousTask task, AppEvent event) {
    }
}
