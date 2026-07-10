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

package net.cdahmedeh.poetwrite.ui.app;

import lombok.Getter;
import lombok.SneakyThrows;
import net.cdahmedeh.poetwrite.service.interfaces.LazyService;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Singleton
public class PersistenceHandler extends LazyService {
    public static final String DEFAULT_FILE_EXTENSION = "poem";

    public static enum FileStatus {
        UNKNOWN, NEW, OPENED, UNCHANGED, CHANGED, SAVED;
    }

    @Getter
    private Path currentFile;

    @Getter
    private String content;

    @Getter
    private String original;

    public FileStatus fileStatus =  FileStatus.NEW;

    @Inject
    protected PersistenceHandler(TaskBus taskBus) {
        super(taskBus);
    }

    @Override
    public String name() {
        return "Persistence Handler";
    }

    @Override
    @SneakyThrows
    protected void init() {
        currentFile = Files.createTempFile("poet-write-temp", ".poem");
        this.content = "";
        this.original = "";
        fileStatus = FileStatus.NEW;
    }

    public FileStatus status() {
        return fileStatus;
    }

    @SneakyThrows
    public void create() {
        currentFile = Files.createTempFile("poet-write-temp", ".poem" );
        this.content = "";
        this.original = "";
        fileStatus = FileStatus.NEW;
    }

    public void update(String text) {
        this.content = text;
        if (content.equals(original) == false) {
            fileStatus = FileStatus.CHANGED;
        } else {
            fileStatus = FileStatus.UNCHANGED;
        }
    }

    @SneakyThrows
    public void save(File file) {
        if (fileStatus == FileStatus.NEW) {
            this.currentFile = file.toPath();
        }
        Files.writeString(currentFile, content);
        this.original = content;
        fileStatus = FileStatus.SAVED;
    }

    @SneakyThrows
    public void open(File file) {
        String content = Files.readString(file.toPath());
        this.currentFile = file.toPath();
        this.content = content;
        this.original = content;
        this.fileStatus = FileStatus.OPENED;
    }

//    @SneakyThrows
//    public void saveAs(File file) {
//        if (file.exists()) {
//            this.newFile = false;
//        } else {
//            this.newFile = true;
//        }
//        this.currentFile = file.toPath();
//        Files.writeString(currentFile, content);
//    }

    @SneakyThrows
    public void save() {
        Files.writeString(currentFile, content);
        this.original = content;
        this.fileStatus = FileStatus.SAVED;
    }
}
