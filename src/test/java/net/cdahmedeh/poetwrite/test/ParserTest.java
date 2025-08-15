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

package net.cdahmedeh.poetwrite.test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import lombok.SneakyThrows;
import net.cdahmedeh.poetwrite.constructor.LineConstructor;
import net.cdahmedeh.poetwrite.domain.Line;
import net.cdahmedeh.poetwrite.tools.FileTools;
import net.cdahmedeh.poetwrite.tools.JsonTools;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;

/**
 * This is to ensure consistent behaviour with the parser.
 *
 * The test picks up some kind of input, lets PoetWrite do the parsing, and then
 * compares against an expected output.
 *
 * Rather than making tests unreadable with something like AssertJ, I decided to
 * use JSON to determine the expected output. Then to ensure that the entity
 * structure is intact, it is converted to JSON and compared.
 *
 * The test files can be found in parser-test-cases.
 *     *.input are a poetry line that will be analyzed and parsed
 *     *.json are the expected output.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ParserTest {
    public static final String PARSER_TEST_CASES_FOLDER = "parser-test-cases/";

    @Test
    @SneakyThrows
    void testParserExampleCases() {
        List<File> inputFiles = FileTools.listFiles(PARSER_TEST_CASES_FOLDER, "input");
        List<File> expectedFiles = FileTools.listFiles(PARSER_TEST_CASES_FOLDER, "json");

        int size = Math.min(inputFiles.size(), expectedFiles.size());

        for (int i = 0; i < size; i++) {
            File inputFile = inputFiles.get(i);
            File expectedFile = expectedFiles.get(i);

            String input = FileTools.readFile(inputFile);
            String expected = FileTools.readFile(expectedFile);

            Line line = LineConstructor.fromText(input);
            String actual = JsonTools.toJson(line);

            assertJsonEquals(expected, actual);
        }
    }
}
