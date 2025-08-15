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

package net.cdahmedeh.poetwrite.tools;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * @author Ahmed El-Hajjar
 *
 * Some methods to deal with basic file operations and reading. Java's native
 * File handling used to be incredibly convoluted and even Apache Commons IO
 * still isn't terse enough.
 *
 * Not the cleanest code but gets the job done. TODO: No error handling of any
 * kind.
 */
public class FileTools {
    // not recurisve

    /**
     * List all files in a folder with a certain extension. Not recursive.
     */
    @SneakyThrows
    public static List<File> listFiles(String folder, String extension) {
        URL url = FileTools.class.getClassLoader().getResource(folder);
        File file = new File(url.toURI());
        List<File> files = new ArrayList<>(FileUtils.listFiles(file, new String[]{extension}, false));
        files.sort(Comparator.comparing(File::getName));
        return files;
    }

    /**
     * Reads the provided file and outputs its content as a string.
     */
    @SneakyThrows
    public static String readFile(File file) {
        return FileUtils.readFileToString(file, Charset.defaultCharset());
    }
}
