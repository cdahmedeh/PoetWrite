package net.cdahmedeh.poetwrite.component;

import dagger.Component;
import net.cdahmedeh.poetwrite.analyzer.RhymeAnalyzer;
import net.cdahmedeh.poetwrite.analyzer.SyllableAnalyzer;
import net.cdahmedeh.poetwrite.computer.RhymeComputer;
import net.cdahmedeh.poetwrite.computer.WordComputer;

import javax.inject.Singleton;

@Component
@Singleton
public interface TestComponent {
    RhymeAnalyzer getRhymeAnalyzer();
    RhymeComputer getRhymeComputer();
    SyllableAnalyzer getSyllableAnalyzer();
    WordComputer getWordComputer();
}
