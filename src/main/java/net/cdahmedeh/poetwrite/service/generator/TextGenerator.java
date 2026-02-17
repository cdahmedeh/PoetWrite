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

package net.cdahmedeh.poetwrite.service.generator;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import lombok.SneakyThrows;
import net.cdahmedeh.poetwrite.service.analyzer.PhonemeAnalyzer;
import net.cdahmedeh.poetwrite.service.analyzer.RhymeAnalyzer;
import net.cdahmedeh.poetwrite.service.analyzer.SyllableAnalyzer;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import net.cdahmedeh.poetwrite.service.interfaces.LazyService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TextGenerator extends LazyService {
    public static final int DEFAULT_MIN_PARAGRAPHS = 10;
    public static final int DEFAULT_MAX_PARAGRAPHS = 25;

    private final SyllableAnalyzer syllableAnalyzer;
    private final PhonemeAnalyzer phonemeAnalyzer;
    private final RhymeAnalyzer rhymeAnalyzer;

    private Lorem lorem;

    @Inject
    public TextGenerator(TaskBus taskBus, SyllableAnalyzer syllableAnalyzer, PhonemeAnalyzer phonemeAnalyzer, RhymeAnalyzer rhymeAnalyzer) {
        super(taskBus);
        this.syllableAnalyzer = syllableAnalyzer;
        this.phonemeAnalyzer = phonemeAnalyzer;
        this.rhymeAnalyzer = rhymeAnalyzer;
    }

    @Override
    public String name() {
        return "Random Text Generator";
    }

    @Override
    @SneakyThrows
    public void init() {
//        try {
//            Thread.sleep(4000);
//        } catch (InterruptedException e) {
//        }
        lorem = LoremIpsum.getInstance();
    }

    @SneakyThrows
    public String generate() {
//        Thread.sleep(new Random().nextInt(1000));
        return lorem.getParagraphs(DEFAULT_MIN_PARAGRAPHS, DEFAULT_MAX_PARAGRAPHS);
    }

    @SneakyThrows
    public String make(long sleep, String word) {
        Thread.sleep(sleep);
        return word;
    }
}
