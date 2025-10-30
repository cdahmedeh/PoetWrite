package net.cdahmedeh.poetwrite.analysis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Word;

import java.util.List;

@RequiredArgsConstructor
public class RhymeAnalysis {
    @Getter
    private final Word wordA;

    @Getter
    private final Word wordB;

    @Getter
    @Setter
    private List<Phoneme> phonemesA = null;

    @Getter
    @Setter
    private List<Phoneme> phonemesB = null;

    @Getter
    @Setter
    private Integer numberOfRhymeSyllables = null;

    public boolean arePhonemesAnalyzed() {
        return phonemesA != null && phonemesB != null;
    }

    public boolean isRhymeAnalyzed() {
        return numberOfRhymeSyllables != null;
    }
}
