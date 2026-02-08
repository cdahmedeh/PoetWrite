package net.cdahmedeh.poetwrite.ui;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import net.cdahmedeh.poetwrite.generator.TextGenerator;

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
