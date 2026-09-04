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

package net.cdahmedeh.poetwrite.ui.viewcontroller;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import net.cdahmedeh.poetwrite.annotation.Duplicated;
import net.cdahmedeh.poetwrite.lib.analysis.PatternAnalysis;
import net.cdahmedeh.poetwrite.lib.analysis.PoemAnalysis;
import net.cdahmedeh.poetwrite.lib.analysis.PoemSyllablesAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Poem;
import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.query.event.QueryPreviewedEvent;
import net.cdahmedeh.poetwrite.query.event.QueryStepExecutedEvent;
import net.cdahmedeh.poetwrite.query.holder.AutoCompleteTreeHolder;
import net.cdahmedeh.poetwrite.query.event.QueryTreeBuiltEvent;
import net.cdahmedeh.poetwrite.query.interfaces.QueryStep;
import net.cdahmedeh.poetwrite.lib.domain.Line;
import net.cdahmedeh.poetwrite.service.analyzer.DefinitionAnalyzer;
import net.cdahmedeh.poetwrite.service.analyzer.MeterAnalyzer;
import net.cdahmedeh.poetwrite.service.analyzer.PartOfSpeechAnalyzer;
import net.cdahmedeh.poetwrite.service.analyzer.PatternAnalyzer;
import net.cdahmedeh.poetwrite.service.analyzer.PoemAnalyzer;
import net.cdahmedeh.poetwrite.service.analyzer.PoemSyllablesAnalyzer;
import net.cdahmedeh.poetwrite.service.indexer.HoverContext;
import net.cdahmedeh.poetwrite.service.indexer.PoemLookupIndexer;
import net.cdahmedeh.poetwrite.ui.event.hover.HoverAnalyzedEvent;
import net.cdahmedeh.poetwrite.ui.event.parsing.*;
import net.cdahmedeh.poetwrite.ui.event.file.SaveFileEvent;
import net.cdahmedeh.poetwrite.ui.event.request.AutoCompleteWizardRequestedEvent;
import net.cdahmedeh.poetwrite.ui.event.request.SaveFileRequestedEvent;
import net.cdahmedeh.poetwrite.ui.services.ApplicationHandler;
import net.cdahmedeh.poetwrite.ui.services.PersistenceManager;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.viewmodel.MainViewModel;

import java.io.File;
import java.util.NavigableMap;


public class MainViewController extends ViewController<MainViewModel> {
    private final ApplicationHandler applicationHandler;
    private final PersistenceManager persistenceManager;

    private final PoemSyllablesAnalyzer poemSyllablesAnalyzer;
    private final PatternAnalyzer patternAnalyzer;
    private final PoemLookupIndexer poemLookupIndexer;

    private final PoemAnalyzer poemAnalyzer;

    private final AutoCompleteTreeHolder autoCompleteTreeHolder;

    private final DefinitionAnalyzer definitionAnalyzer;
    private final PartOfSpeechAnalyzer partOfSpeechAnalyzer;
    private final MeterAnalyzer meterAnalyzer;

    @AssistedInject
    public MainViewController(@Assisted MainViewModel viewModel, TaskBus taskBus, ApplicationHandler applicationHandler, PersistenceManager persistenceManager, PoemSyllablesAnalyzer poemSyllablesAnalyzer, PatternAnalyzer patternAnalyzer, PoemLookupIndexer poemLookupIndexer, PoemAnalyzer poemAnalyzer, AutoCompleteTreeHolder autoCompleteTreeHolder, DefinitionAnalyzer definitionAnalyzer, PartOfSpeechAnalyzer partOfSpeechAnalyzer, MeterAnalyzer meterAnalyzer) {
        super(viewModel, taskBus);
        this.applicationHandler = applicationHandler;
        this.persistenceManager = persistenceManager;
        this.poemSyllablesAnalyzer = poemSyllablesAnalyzer;
        this.patternAnalyzer = patternAnalyzer;
        this.poemLookupIndexer = poemLookupIndexer;
        this.poemAnalyzer = poemAnalyzer;
        this.autoCompleteTreeHolder = autoCompleteTreeHolder;
        this.definitionAnalyzer = definitionAnalyzer;
        this.partOfSpeechAnalyzer = partOfSpeechAnalyzer;
        this.meterAnalyzer = meterAnalyzer;
    }

    /**
     * Parsers the entire poem into the Poem entity structure.
     * @param content
     */
    public void parse(String content) {
        PoemAnalyzedEvent event = new PoemAnalyzedEvent();
        taskBus.submit("Parsing Poem", event, () -> {
            Poem poem = new Poem(content);
            PoemAnalysis poemAnalysis = poemAnalyzer.get(poem);
            event.setPoem(poemAnalysis.getParsed());
        });
    }

    /**
     * Analyzes the syllable lengths per line in the poem.
     */
    public void analyzeSyllables(Poem poem) {
        PoemSyllablesAnalyzedEvent event = new PoemSyllablesAnalyzedEvent();
        taskBus.submit("Analyze Poem Syllables", event, () -> {
            PoemSyllablesAnalysis poemSyllablesAnalysis = poemSyllablesAnalyzer.get(poem);
            event.setAnalysis(poemSyllablesAnalysis);
        });
    }

    public void analyzePattern(Poem poem) {
        PoemPatternAnalyzedEvent event = new PoemPatternAnalyzedEvent();
        taskBus.submit("Analyze Poem Pattern", event, () -> {
            PatternAnalysis patternAnalysis = patternAnalyzer.get(poem);
            event.setPatternAnalysis(patternAnalysis);
        });
    }

    public void getDefinition(Word word) {
        HoverAnalyzedEvent event = new HoverAnalyzedEvent(word);
        taskBus.submit("Definition: " + word.getWord(), event, () -> {
            event.setAnalysis(definitionAnalyzer.get(word));
        });
    }

    public void getPartOfSpeech(Word word, Line line) {
        HoverAnalyzedEvent event = new HoverAnalyzedEvent(word);
        taskBus.submit("Part of Speech: " + word.getWord(), event, () -> {
            event.setAnalysis(partOfSpeechAnalyzer.get(line));
        });
    }

    public void getMeter(Word word, Line line) {
        HoverAnalyzedEvent event = new HoverAnalyzedEvent(word);
        taskBus.submit("Meter: " + word.getWord(), event, () -> {
            event.setAnalysis(meterAnalyzer.get(line));
        });
    }

    public void indexPoem(Poem poem) {
        WordPositionIndexedEvent event = new WordPositionIndexedEvent();
        taskBus.submit("Index Poem", event, () -> {
            NavigableMap<Integer, HoverContext> index = poemLookupIndexer.index(poem);
            event.setIndex(index);
        });
    }

    // When the user wants the auto complete dialog after pressing control+enter
    // This is will not build the tree or directly caused it to appear. It's
    // just a request.
    public void requestAutoComplete() {
        AutoCompleteWizardRequestedEvent event = new AutoCompleteWizardRequestedEvent();
        taskBus.submit("Request Auto Complete", event, () -> {
            event.setRequested(true);
        });
    }

    // After receiving the appropriate event, the tree starts to be built.
    // Keep in mind, this is seperate from requestAutoComplete as we are
    // preparing for cases when the feature set grows and the tree will be built
    // slowly. Remember, again, the query tree builds step-by-step, not the
    // entire thing.
    public void buildAutoCompleteTree() {
        QueryTreeBuiltEvent event = new QueryTreeBuiltEvent();
        taskBus.submit("Building Auto Complete Tree", event, () -> {
            event.setRoot(autoCompleteTreeHolder.tree());
        });
    }

    // Called after the user selected a steps and we want to see the next ones.
    public void executeQueryStep(QueryStep step) {
        QueryStepExecutedEvent event = new QueryStepExecutedEvent(step);
        taskBus.submit("Query: " + step.getName(), event, () -> {
            event.getSteps().addAll(step.resolve());
        });
    }

    // Called when we want to see the preview of a highlighted step.
    public void previewQueryStep(QueryStep step) {
        QueryPreviewedEvent event = new QueryPreviewedEvent(step);
        taskBus.submit("Preview: " + step.getName(), event, () -> {
            event.setText(step.render());
        });
    }

    @AssistedFactory
    public interface MainViewControllerFactory {
        MainViewController create(MainViewModel mainViewModel);
    }

    /**
     * Content in the text editor has changed. Notify the persistence manager.
     */
    public void update(String content) {
        EditorContentChangedEvent event = new EditorContentChangedEvent();
        taskBus.submit("Updating Content", event, new Runnable() {
            @Override
            public void run() {
                persistenceManager.update(content);
                event.setStatus(persistenceManager.status());
            }
        });
    }

    /**
     * Request a save. Check if the save selection dialog is needed.
     */
    public void ask(File selectedFile) {
        SaveFileRequestedEvent event = new SaveFileRequestedEvent();
        taskBus.submit("Saving Poem", event, () -> {
            event.setDialogNeeded(true);
        });
    }

    /**
     * Save the loaded file onto disk.
     */
    public void save() {
        SaveFileEvent event = new SaveFileEvent();

        taskBus.submit("Saving Poem", event, () -> {
            persistenceManager.save();
            event.setFile(persistenceManager.getFile().getFileName().toString());
        });
    }

    /**
     * Save a selected file onto the disk.
     */
    public void save(File selectedFile) {
        SaveFileEvent event = new SaveFileEvent();

        taskBus.submit("Saving Poem", event, () -> {
            persistenceManager.save(selectedFile);
            event.setFile(persistenceManager.getFile().getFileName().toString());
        });
    }

    /**
     * Request to have the application closed. Application handler will kindly
     * wait until all taskbus tasks are done.
     */
    @Duplicated("MenuViewController.closeApp()")
    public void closeApp() {
        applicationHandler.close();
    }

}
