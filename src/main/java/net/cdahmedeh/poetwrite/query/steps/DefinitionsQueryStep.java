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



package net.cdahmedeh.poetwrite.query.steps;

import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.query.interfaces.*;
import net.cdahmedeh.poetwrite.service.analyzer.SynonymAnalyzer;
import net.cdahmedeh.poetwrite.service.analyzer.SyllableAnalyzer;
import net.cdahmedeh.poetwrite.ui.constant.IconConstants;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * "definitions" branch: type a word, get its senses. Hard-coded dictionary.
 */
@Singleton
public class DefinitionsQueryStep extends QueryStep {

    private final SyllableAnalyzer syllableAnalyzer;
    private final SynonymAnalyzer synonymAnalyzer;

    @Inject
    public DefinitionsQueryStep(SyllableAnalyzer syllableAnalyzer, SynonymAnalyzer synonymAnalyzer) {
        super("definitions");
        this.syllableAnalyzer = syllableAnalyzer;
        this.synonymAnalyzer = synonymAnalyzer;
        icon(IconConstants.DICTIONARY_ICON_PATH);
        search(QuerySearch::new);
        command(this::lookup);
    }

    public QueryStep steps() {
        return this;
    }

    private List<QueryStep> lookup(QueryStep step) {
        String typed = step.getSearch().getText().trim().toLowerCase();
        if (typed.isEmpty()) {
            return List.of();
        }

        List<QueryStep> steps = new ArrayList<>();
        for (Sense sense : senses(typed)) {
            Word word = new Word(sense.word());

            // First three came out of the lookup itself. Last two are real
            // analyses, so they land on their own.
            steps.add(step(sense.label())
                    .preview(s -> "<b>" + sense.word() + "</b>")
                    .preview(s -> "<i>" + sense.partOfSpeech() + "</i>")
                    .preview(s -> sense.gloss())
                    .preview("Syllables", s -> syllableAnalyzer.get(word).getNumberOfSyllables() + " syllables")
                    .preview("Synonyms", s -> String.join(", ", synonymAnalyzer.get(word).getSynonyms())));
        }
        return steps;
    }

    private List<Sense> senses(String word) {
        return switch (word) {
            case "darkness" -> List.of(
                    new Sense("darkness", "noun", "The partial or total absence of light."),
                    new Sense("darkness", "noun", "Wickedness or evil."),
                    new Sense("darkness", "noun", "Secrecy; a state of being concealed."));
            case "fortress" -> List.of(
                    new Sense("fortress", "noun", "A military stronghold."),
                    new Sense("fortress", "verb", "To furnish with a fortress."));
            default -> List.of(
                    new Sense(word, "noun", "No entry -- hard-coded demo dictionary."));
        };
    }

    public record Sense(String word, String partOfSpeech, String gloss) {
        public String label() {
            return word + " (" + partOfSpeech + ")";
        }
    }

}
