/**
 * PoetWrite - A Poetry Writing Application
 * Copyright (C) 2025 Ahmed El-Hajjar
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

package net.cdahmedeh.poetwrite.ui.services;

import lombok.Getter;
import lombok.SneakyThrows;
import net.cdahmedeh.poetwrite.service.interfaces.LazyService;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import org.jspecify.annotations.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This basically handles loading and saving poems from a file. It's mostly one
 * way, as in ViewControllers will typically call this to do a certain save or
 * new operation. In other cases, during a load, it will return the contents
 * of the file.
 *
 * The .poem file format is just a plain-text file.
 *
 * TODO: Atomic and safe saving. Right now, any kind of failure will result in
 *       a lost file, or corrupted content.
 * TODO: Automatic handling of appending .poem extension if user doesn't provide
 *       one.
 * TODO: Right now, when a new file is created, it is stored in a temporary
 *       file. Maybe in the future, this could be the 'swap' file ala Vim.
 * TODO: Handle what happens with invalid file types.
 *
 * BUG: When PoetWrite opens for the first time, the UI is not aware of the file
 *      name. Should have this called upon application opening while the UI
 *      starting up.
 */
@Singleton
public class PersistenceManager extends LazyService {
    // File extension for Poem files.
    // TODO: Will be used later for automatic appending of file extension.
    public static final String DEFAULT_FILE_EXTENSION = "poem";

    // The base name for the temporary file when it is created.
    private static final String TEMP_FILE_PREFIX = "poet-write-temp-" ;

    // What state is the loaded file at.
    // Very important, this isn't the status of the saved file.
    // It indicates in what state the persistence manager is handling the file.
    public static enum FileStatus {
        UNKNOWN,        // Default status, when the application is loaded for the first time.
                        // In theory, accessing the status should
        NEW,            // A new file was created. But has no modifications yet.
        OPENED,         // A file was just opened, but no modifications yet.
        UNCHANGED,      // After content is changed, it can revert to this status.
                        // It's also used to keep track if the user undoes changes
                        // This will be true again.
        CHANGED,        // The content has been changed. Used by the UI to notify
                        // the user that some changes were made.
        SAVED;          // The content was saved into the file. But no edits have been made yet.
    }

    // The file that has been loaded and is being dealt with.
    @Getter
    private Path file;

    // This is the content that is displayed in the UI. So it's not always the
    // same as what is in the original file.
    @Getter
    private String content;

    // The contents of the file on disk. Compares between content in text editor
    // and the original text in the file. To know if changes have been made.
    @Getter
    private String original;

    // See comment above the FileStatus enum.
    @Getter
    private FileStatus status =  FileStatus.NEW;

    @Inject
    protected PersistenceManager(TaskBus taskBus) {
        super(taskBus);
    }

    @Override
    public String name() {
        return "Persistence Handler";
    }

    public FileStatus status() {
        return status;
    }

    @Override
    @SneakyThrows
    protected void init() {
        createNewFile();
    }

    /**
     * Called when we want to create a new file.
     */
    @SneakyThrows
    public void create() {
        createNewFile();
    }

    /**
     * When the content changes in the text editor changes, it is update
     * directly in here and stored in the manager.
     */
    public void update(String text) {
        this.content = text;
        if (hasContentChanged()) {
            status = FileStatus.CHANGED;
        } else {
            status = FileStatus.UNCHANGED;
        }
    }

    /**
     * Save the content to disk. For specific file. Namely for the Save As
     * function where the user will want to store the file somewhere else.
     */
    @SneakyThrows
    public void save(File file) {
        if (status == FileStatus.NEW) {
            this.file = file.toPath();
        }
        writeToDisk(file.toPath());
        this.original = content;
        status = FileStatus.SAVED;
    }

    /**
     * Save the current file to disk.
     */
    @SneakyThrows
    public void save() {
        writeToDisk(file);
        this.original = content;
        this.status = FileStatus.SAVED;
    }

    /**
     * Load a new file.
     */
    @SneakyThrows
    public void open(File file) {
        String content = readFileContentFromDisk(file);
        this.file = file.toPath();
        this.content = content;
        this.original = content;
        this.status = FileStatus.OPENED;
    }



    /**
     * Create a new file.
     * TODO: For now, it creates a temp file. But the editor knows if someone
     *       tries to save a new file with changes in it. It will prompt the
     *       user for a file selection.
     */
    @SneakyThrows
    private void createNewFile() {
        file = Files.createTempFile(TEMP_FILE_PREFIX, "." +  DEFAULT_FILE_EXTENSION);
        this.content = "";
        this.original = "";
        status = FileStatus.NEW;
    }

    /**
     * Checks if the content being edited is different than what is already in
     * the file on disk.
     */
    private boolean hasContentChanged() {
        return content.equals(original) == false;
    }

    /**
     * Write the changed content into the provided file.
     *
     * TODO: Not atomic or fool-proof in any way.
     */
    private void writeToDisk(Path file) throws IOException {
        Files.writeString(file, content);
    }

    private static @NonNull String readFileContentFromDisk(File file) throws IOException {
        return Files.readString(file.toPath());
    }
}
