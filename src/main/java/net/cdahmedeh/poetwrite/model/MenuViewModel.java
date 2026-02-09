package net.cdahmedeh.poetwrite.model;

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import net.cdahmedeh.poetwrite.engine.AppEvent;
import net.cdahmedeh.poetwrite.async.AsynchronousTaskHandler;

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
