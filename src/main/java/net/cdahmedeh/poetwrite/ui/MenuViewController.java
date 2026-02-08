package net.cdahmedeh.poetwrite.ui;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

public class MenuViewController extends ViewController<MenuViewModel> {
    @AssistedInject
    protected MenuViewController(@Assisted MenuViewModel viewModel, AsynchronousTaskHandler taskHandler) {
        super(viewModel, taskHandler);
    }
}
