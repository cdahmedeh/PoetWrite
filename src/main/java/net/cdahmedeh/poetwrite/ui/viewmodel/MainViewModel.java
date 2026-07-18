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

package net.cdahmedeh.poetwrite.ui.viewmodel;

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import net.cdahmedeh.poetwrite.lib.analysis.PatternAnalysis;
import net.cdahmedeh.poetwrite.lib.analysis.PoemSyllablesAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Poem;
import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.service.indexer.PoemLookupIndexer;
import net.cdahmedeh.poetwrite.ui.services.PersistenceManager;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.event.*;
import net.cdahmedeh.poetwrite.ui.async.AppTask;

import java.util.Map;
import java.util.NavigableMap;

public class MainViewModel extends ViewModel {
    // This holds the actual Poem as constructed by the parser.
    // It may seem crude to parse every time, but it's proven to be so fast
    // that it doesn't justify something like a diff system.
    // See "Poem Analysis Implementation and Cache Design" for the motive.
    private BehaviorSubject<Poem> poem = BehaviorSubject.createDefault(new Poem());
    public Observable<Poem> poem() { return this.poem.hide(); }

    // Holds the syllable counts for the poem, namely each line inside the Poem
    // entity.
    // TODO: Will eventually need to seperate long running analysis and ones
    //       that are quicker.
    private BehaviorSubject<PoemSyllablesAnalysis>  poemSyllablesAnalysis = BehaviorSubject.createDefault(new PoemSyllablesAnalysis(new Poem()));
    public Observable<PoemSyllablesAnalysis> poemSyllablesAnalysis() { return this.poemSyllablesAnalysis.hide(); }

    // Holds the pattern groups for the poem. Essentially, a letter for each
    // group.
    private BehaviorSubject<PatternAnalysis> patternAnalysis = BehaviorSubject.create();
    public Observable<PatternAnalysis> patternAnalysis() { return this.patternAnalysis.hide(); }

    // Holds the relationship between text positions in the content in the
    // editor and entities.
    // TODO: Right now, it's only words.
    private BehaviorSubject<NavigableMap<Integer, Word>> poemIndex = BehaviorSubject.create();
    public Observable<NavigableMap<Integer, Word>> poemIndex() { return this.poemIndex.hide(); }

    // What is in the content editor. Mostly to modify the content editor
    // when something changes. Not really intended to be updated if user
    // makes a change to it.
    // TODO: See if the view could refer to this for saving/loading.
    private BehaviorSubject<String> editorContent = BehaviorSubject.createDefault("");
    public Observable<String> editorContent() {
        return this.editorContent.hide();
    }

    // During a save operation, determines if the user should get a file
    // selection dialog.
    // Examples: Saves a new file that isn't anywhere yet. When using the Save
    //           As function.
    // NOTE : Very important that this is PublishSubject type. Otherwise, the
    //        application will try to load the save dialog upon application
    //        start up. BehaviourSubject needs sa default value, so even upon
    //        instantiating it, it throws an event.
    private PublishSubject<Boolean> dialogNeeded = PublishSubject.create();
    public Observable<Boolean> dialogNeeded() {
        return this.dialogNeeded.hide();
    }

    // The name of the file being dealt with. Not the full path, just for
    // displaying it in the title bar.
    private BehaviorSubject<String> fileName = BehaviorSubject.createDefault("");
    public Observable<String> fileName() {
        return this.fileName.hide();
    }

    // The status of the file based on the persistence manager. While file
    // status has several types of statuses, we only really care if the file
    // was changed or not. To display in the title bar.
    private BehaviorSubject<PersistenceManager.FileStatus> fileStatus = BehaviorSubject.createDefault(PersistenceManager.FileStatus.UNKNOWN);
    public Observable<PersistenceManager.FileStatus> fileStatus() {
        return this.fileStatus.hide();
    }

    @AssistedInject
    public MainViewModel(TaskBus taskBus) {
        super(taskBus);
    }

    @AssistedFactory
    public interface MainViewModelFactory {
        MainViewModel create();
    }

    @Override
    protected void listen(AppTask task, AppEvent event) {
        // If content changes from the UI side, it updates the status for
        // display in the title bar.
        //
        // TODO: This is going to be the most important part and key event for
        //       handling analysis and any kind of display.
        // TODO: Nothing is being done with the content yet, but some of the
        //       assistances will do exactly that.
        if (event instanceof ContentChangedEvent contentChangedEvent) {
            String text = contentChangedEvent.getContent();
            this.fileStatus.onNext(contentChangedEvent.getStatus());
        }

        // Upon succesful parsing of a Poem, sent it to the View. Right now,
        // the view just uses to send it to the controller for analysis.
        //
        // TODO: Will eventually need this for when we have analysis that only
        //       show on hover. Like definitions or part of speech.
        if (event instanceof ParsePoemEvent parsePoemEvent) {
            if (parsePoemEvent.getPoem() != null) {
                this.poem.onNext(parsePoemEvent.getPoem());
            }
        }

        // When the syllables per line has been calculated. Right now this is
        // lightning quick.
        //
        // TODO: Implement debouncing.
        if (event instanceof LineSyllablesAnalyzedEvent lineSyllablesAnalyzedEvent) {
            this.poemSyllablesAnalysis.onNext(lineSyllablesAnalyzedEvent.getAnalysis());
        }

        // When the pattern groups have been calculated/detected. This seems
        // pretty quick too. But once rhyming detection gets more sophisticated
        // this may get much slower.
        // TODO: Performance considerations.
        if (event instanceof PoemPatternAnalyzedEvent poemPatternAnalyzedEvent) {
            this.patternAnalysis.onNext(poemPatternAnalyzedEvent.getPatternAnalysis());
        }

        // Upon indexing the Poem to map the character positions to the original
        // entities as needed for the word hover highlight.
        if (event instanceof IndexedPoemEvent indexedPoemEvent) {
            this.poemIndex.onNext(indexedPoemEvent.getIndex());
        }

        // Upon having the poem index to map text positions to an entity in the
        // poem. Right now, used for providing information on a word when
        // hoving over it.

        // Upon a file successfully saved.
        // Just to change the status on the title bar for now.
        if (event instanceof SaveEvent saveEvent) {
            this.fileStatus.onNext(saveEvent.getFileStatus());
        }

        // A new file is loaded.
        // Updates filename and status for title bar.
        // Clears the editor.
        if (event instanceof NewFileEvent newFileEvent) {
            this.fileName.onNext(newFileEvent.getFile());
            this.fileStatus.onNext(newFileEvent.getFileStatus());
            this.editorContent.onNext("");
        }

        // Called when save or save as is requested. This will decide if a
        // save dialog is needed. This is not the same as actually saving the
        // file.
        if (event instanceof SaveRequestedEvent dialogNeededEvent) {
            this.dialogNeeded.onNext(dialogNeededEvent.isDialogNeeded());
        }

        // When a file is loading into the persistence manager. It mainly
        // just fills the text area with the content.
        if (event instanceof FileOpenedEvent fileOpenedEvent) {
            this.fileName.onNext(fileOpenedEvent.getFile());
            this.fileStatus.onNext(fileOpenedEvent.getFileStatus());
            this.editorContent.onNext(fileOpenedEvent.getContent());
        }

        // Kind of redundant. But updates the status of the file for the
        // titlebar.
        // TODO: Probably sending some rogue events.
        if (event instanceof FileEvent fileEvent) {
            this.fileName.onNext(fileEvent.getFile());
            this.fileStatus.onNext(fileEvent.getFileStatus());
        }
    }
}
