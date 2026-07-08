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
import lombok.Setter;
import lombok.SneakyThrows;
import net.cdahmedeh.poetwrite.service.interfaces.LazyService;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.ui.event.ContentUpdateEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Singleton
public class PersistenceHandler extends LazyService {
    @Serial
    private Path currentFile;

    @Getter
    private String content;

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
        currentFile = Files.createTempFile("poet-write-temp", ".poem" );
        this.content = "";
    }

    @SneakyThrows
    public void create() {
        currentFile = Files.createTempFile("poet-write-temp", ".poem" );
        this.content = "";
    }

    public void update(String text) {
        this.content = text;
    }

    @SneakyThrows
    public void save() {
        Files.writeString(currentFile, content);
    }

    @SneakyThrows
    public void open(File file) {
        String content = Files.readString(file.toPath());
        this.currentFile = file.toPath();
        this.content = content;
    }

    @SneakyThrows
    public void saveAs(File file) {
        this.currentFile = file.toPath();
        Files.writeString(currentFile, content);
    }
}
