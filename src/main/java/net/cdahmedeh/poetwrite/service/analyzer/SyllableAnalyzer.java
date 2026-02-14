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

import net.cdahmedeh.poetwrite.lib.analysis.PhonemeAnalysis;
import net.cdahmedeh.poetwrite.lib.analysis.SyllableAnalysis;
import net.cdahmedeh.poetwrite.service.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.lib.domain.Phoneme;
import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.ui.async.AsynchronousTaskHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * @author Ahmed El-Hajjar
 *
 * This analyzer does some phoneme analysis and outputs a result as a
 * PhonemeAnalysis object. The output is stored in the AnalysisCache which can
 * retrieved later.
 *
 * All this does for now is count the number of syllables in a word. Which is
 * taken from PhonemeAnalyzer.
 */
@Singleton
public class SyllableAnalyzer extends FeatureAnalyzer<Word, SyllableAnalysis> {
    PhonemeAnalyzer phonemeComputer;

    @Inject
    SyllableAnalyzer(
            AnalysisCache analysisCache,
            AsynchronousTaskHandler taskHandler,
            PhonemeAnalyzer phonemeComputer) {
        super(analysisCache, taskHandler);
        this.phonemeComputer = phonemeComputer;
    }

    public SyllableAnalysis get(Word word) {
        return get(word, SyllableAnalysis.class);
    }

    @Override
    /* package */ void analyze(Word word, SyllableAnalysis analysis) {
        PhonemeAnalysis phonemeAnalysis = phonemeComputer.get(word);

        List<Phoneme> phonemes = phonemeAnalysis.getPhonemes();

        int syllables = (int) phonemes.stream()
                .filter(Phoneme::isVowel)
                .count();

        analysis.setNumberOfSyllables(syllables);
    }

    @Override
    public String name() {
        return "Syllable Analyzer";
    }

    @Override
    protected void init() {

    }
}
