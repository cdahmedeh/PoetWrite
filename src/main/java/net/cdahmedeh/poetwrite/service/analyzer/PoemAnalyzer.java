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



package net.cdahmedeh.poetwrite.service.analyzer;

import lombok.Getter;
import net.cdahmedeh.poetwrite.lib.analysis.PoemAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Poem;
import net.cdahmedeh.poetwrite.lib.parser.PoemExtendedVisitor;
import net.cdahmedeh.poetwrite.lib.parser.PoemLexer;
import net.cdahmedeh.poetwrite.lib.parser.PoemParser;
import net.cdahmedeh.poetwrite.lib.parser.PoemVisitor;
import net.cdahmedeh.poetwrite.service.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.service.interfaces.LazyService;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Handles Poem parsing through ANTLR. It used to be a static method but I'm
 * working on the autocomplete system, and it needs access to the poem.
 */
@Singleton
public class PoemAnalyzer extends FeatureAnalyzer<Poem, PoemAnalysis> {
    @Inject
    PoemAnalyzer(AnalysisCache analysisCache, TaskBus taskBus) {
        super(analysisCache, taskBus);
    }

    @Override
    public String name() {
        return "Poem Analyzer";
    }

    @Override
    protected void init() {
    }

    public PoemAnalysis get(Poem poem) {
        return get(poem, PoemAnalysis.class);
    }

    @Override
    void analyze(Poem poem, PoemAnalysis analysis) {
        PoemLexer lexer = new PoemLexer(CharStreams.fromString(poem.getText()));
        CommonTokenStream stream = new CommonTokenStream(lexer);
        PoemParser parser = new PoemParser(stream);

        PoemParser.PoemContext context = parser.poem();

        PoemVisitor visitor = new PoemExtendedVisitor();

        Poem parsed = (Poem) visitor.visit(context);
        analysis.setParsed(parsed);
    }
}
