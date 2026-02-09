package net.cdahmedeh.poetwrite.controller;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import net.cdahmedeh.poetwrite.event.TextUpdateEvent;
import net.cdahmedeh.poetwrite.generator.TextGenerator;
import net.cdahmedeh.poetwrite.model.MenuViewModel;
import net.cdahmedeh.poetwrite.async.AsynchronousTaskHandler;

import java.util.Random;

public class MenuViewController extends ViewController<MenuViewModel> {
    private final TextGenerator textGenerator;

    @AssistedInject
    protected MenuViewController(@Assisted MenuViewModel viewModel, AsynchronousTaskHandler taskHandler, TextGenerator textGenerator) {
        super(viewModel, taskHandler);
        this.textGenerator = textGenerator;
    }

    @AssistedFactory
    public interface MenuViewControllerFactory {
        MenuViewController create(MenuViewModel menuViewModel);
    }

    public void generateRandomText() {
        for (int i = 0; i < 10; i++) {
            TextUpdateEvent event = new TextUpdateEvent();
            taskHandler.submit("Generating Random Text " + new Random().nextDouble(), event, () -> {
                String text = textGenerator.generate();
                event.setText(text);
            });
        }
    }
}
