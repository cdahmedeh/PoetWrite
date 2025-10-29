package net.cdahmedeh.poetwrite.analysis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Word;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class WordAnalysis {
    @Getter
    private final Word word;

    @Getter
    @Setter
    private List<Phoneme> phonemes = null;

    @Getter
    @Setter
    private Integer numberOfSyllables = null;

    public boolean isWordAnalyzed() {
        return phonemes != null && numberOfSyllables != null;
    }
}
