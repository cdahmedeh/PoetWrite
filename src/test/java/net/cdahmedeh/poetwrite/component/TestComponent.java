package net.cdahmedeh.poetwrite.component;

import dagger.Component;
import net.cdahmedeh.poetwrite.computer.RhymeComputer;
import net.cdahmedeh.poetwrite.computer.WordComputer;

import javax.inject.Singleton;

@Component
@Singleton
public interface TestComponent {
    RhymeComputer getRhymeComputer();
    WordComputer getWordComputer();
}
