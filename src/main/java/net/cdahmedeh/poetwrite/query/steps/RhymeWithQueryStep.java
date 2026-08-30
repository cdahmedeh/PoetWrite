package net.cdahmedeh.poetwrite.query.steps;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.cdahmedeh.poetwrite.query.interfaces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RhymeWithQueryStep extends QueryStep {

    public RhymeWithQueryStep() {
        super("rhyme with");
    }

    public QueryStep steps() {
        return steps(() -> List.of(
                step("previous line")
                        .preview(() -> new LinePreview(-1))
                        .steps(relationshipsSteps()),
                step("next line")
                        .preview(() -> new LinePreview(+1))
                        .steps(relationshipsSteps()),
                step("matching pattern")
                        .preview(PatternGroupPreview::new)
                        .command(this::findPatternGroups)
        ));
    }

    private Supplier<List<QueryStep>> relationshipsSteps() {
        return () -> List.of(
                step("means").search(QuerySearch::new).command(this::lookupWords),
                step("related to").search(QuerySearch::new).command(this::lookupWords),
                step("sounds like").search(QuerySearch::new).command(this::lookupWords)
        );
    }

    // ---------------------------------------------------------- commands

    // Stands in for patternAnalyzer.get(poem). Runs on the TaskBus.
    private List<QueryStep> findPatternGroups(QueryStep step) {
        sleep(600);

        List<QueryStep> steps = new ArrayList<>();
        steps.add(patternGroup("A (less)"));
        steps.add(patternGroup("B (tion)"));
        steps.add(patternGroup("C (able)"));
        return steps;
    }

    private QueryStep patternGroup(String pattern) {
        return step(pattern)
                .preview(PatternGroupPreview::new)
                .command(step -> {
                    step.getParameters().put(new PatternGroupParameter(pattern));
                    return relationshipsSteps().get();
                });
    }

    // Stands in for the CMU dictionary lookup. Runs on the TaskBus.
    private List<QueryStep> lookupWords(QueryStep step) {
        sleep(400);

        String typed = step.getSearch().getText().trim();
        if (typed.isEmpty()) {
            return List.of();
        }

        PatternGroupParameter group = (PatternGroupParameter)
                step.getParameters().get(PatternGroupParameter.class);
        String pattern = group == null ? "A (less)" : group.getPattern();

        List<QueryStep> steps = new ArrayList<>();
        for (Word word : words(pattern)) {
            steps.add(step(word.label()).preview(() -> new WordPreview(word)));
        }
        return steps;
    }

    // Hardcoded dictionary, keyed by the group picked upstream.
    private List<Word> words(String pattern) {
        return switch (pattern) {
            case "B (tion)" -> List.of(
                    new Word("attention", "AH T EH N SH AH N", 3),
                    new Word("devotion", "D IH V OW SH AH N", 3),
                    new Word("motion", "M OW SH AH N", 2));
            case "C (able)" -> List.of(
                    new Word("unspeakable", "AH N S P IY K AH B AH L", 4),
                    new Word("fadeable", "F EY D AH B AH L", 3));
            default -> List.of(
                    new Word("blackness", "B L AE K N AH S", 2),
                    new Word("vividness", "V IH V IH D N AH S", 3),
                    new Word("fortress", "F AO R T R AH S", 2));
        };
    }

    // -------------------------------------------------------- parameters

    @RequiredArgsConstructor
    public static class PatternGroupParameter extends QueryParameter {
        @Getter
        private final String pattern;
    }

    // ---------------------------------------------------------- previews

    // Hardcoded stand-in for the real poem lines.
    private static final List<String> LINES = List.of(
            "The night was long and starless",
            "A quiet, creeping darkness",
            "And nothing moved at all");

    @RequiredArgsConstructor
    public static class LinePreview extends QueryPreview {
        private final int offset;

        @Override
        public String render(QueryStep step) {
            int index = 1 + offset;
            if (index < 0 || index >= LINES.size()) {
                return "(no line)";
            }
            return LINES.get(index);
        }
    }

    public static class PatternGroupPreview extends QueryPreview {
        @Override
        public String render(QueryStep step) {
            PatternGroupParameter group = (PatternGroupParameter)
                    step.getParameters().get(PatternGroupParameter.class);

            if (group == null) {
                return "Pick a rhyme group from the poem.";
            }

            return switch (group.getPattern()) {
                case "A (less)" -> "A (less)\n\nstarless\nnameless";
                case "B (tion)" -> "B (tion)\n\nattention\ndevotion";
                default -> "C (able)\n\nunspeakable\nfadeable";
            };
        }
    }

    @RequiredArgsConstructor
    public static class WordPreview extends QueryPreview {
        private final Word word;

        @Override
        public String render(QueryStep step) {
            sleep(200);               // pretend we walked a few dictionaries
            return word.text() + "\n\n/" + word.arpaBet() + "/\n"
                    + word.syllables() + " syllables";
        }
    }

    public record Word(String text, String arpaBet, int syllables) {
        public String label() {
            return text + " (" + syllables + ")";
        }
    }

    // ------------------------------------------------------------- utils

    // Only here so the loading state is visible while developing.
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}