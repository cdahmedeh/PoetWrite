package net.cdahmedeh.poetwrite.generator;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import lombok.SneakyThrows;
import net.cdahmedeh.poetwrite.ui.AsynchronousTaskHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Random;

public class TextGenerator extends LazyService {
    public static final int DEFAULT_MIN_PARAGRAPHS = 10;
    public static final int DEFAULT_MAX_PARAGRAPHS = 25;

    private Lorem lorem;

    @Inject
    public TextGenerator(AsynchronousTaskHandler taskHandler) {
        super(taskHandler);
    }

    @Override
    public String name() {
        return "Random Text Generator";
    }

    @Override
    @SneakyThrows
    public void init() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        lorem = LoremIpsum.getInstance();
    }

    @SneakyThrows
    public String generate() {
        Thread.sleep(new Random().nextInt(1000));
        return lorem.getParagraphs(DEFAULT_MIN_PARAGRAPHS, DEFAULT_MAX_PARAGRAPHS);
    }
}
