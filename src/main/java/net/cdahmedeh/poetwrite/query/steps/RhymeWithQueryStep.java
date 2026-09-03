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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.cdahmedeh.poetwrite.query.interfaces.*;
import net.cdahmedeh.poetwrite.ui.constant.IconConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RhymeWithQueryStep extends QueryStep {

    public RhymeWithQueryStep() {
        super("rhyme with");
        icon(IconConstants.RHYME_ICON_PATH);
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

    // Hardcoded dictionary, keyed by the group picked upstream. Everything
    // below is demo data -- the real version comes from the CMU dictionary
    // plus whatever thesaurus we end up wiring in.
    private List<Word> words(String pattern) {
        return switch (pattern) {
            case "B (tion)" -> List.of(
                    new Word("attention", "noun", 3, "at-TEN-tion", "AH0 T EH1 N SH AH0 N",
                            "perfect", "feminine",
                            "Notice taken of someone or something; the directing of the mind to an object.",
                            List.of("notice", "regard", "heed", "scrutiny"),
                            List.of("intention", "invention", "dimension"),
                            "it asks no attention, and gets none"),
                    new Word("devotion", "noun", 3, "de-VO-tion", "D IH0 V OW1 SH AH0 N",
                            "perfect", "feminine",
                            "Love, loyalty, or enthusiasm for a person or cause; religious worship.",
                            List.of("dedication", "allegiance", "piety", "fidelity"),
                            List.of("emotion", "commotion", "promotion"),
                            "a devotion worn thin by weather"),
                    new Word("motion", "noun", 2, "MO-tion", "M OW1 SH AH0 N",
                            "slant", "feminine",
                            "The action or process of moving, or of changing place or position.",
                            List.of("movement", "passage", "drift", "travel"),
                            List.of("ocean", "notion", "potion"),
                            "the slow motion of a curtain"));
            case "C (able)" -> List.of(
                    new Word("unspeakable", "adjective", 4, "un-SPEAK-a-ble", "AH0 N S P IY1 K AH0 B AH0 L",
                            "perfect", "dactylic",
                            "Too great, too bad, or too sacred to be expressed in words.",
                            List.of("unutterable", "ineffable", "dreadful", "nameless"),
                            List.of("unbreakable", "unshakeable", "untakeable"),
                            "an unspeakable quiet in the hall"),
                    new Word("fadeable", "adjective", 3, "FADE-a-ble", "F EY1 D AH0 B AH0 L",
                            "slant", "dactylic",
                            "Liable to lose colour, brightness, or intensity over time.",
                            List.of("perishable", "impermanent", "transient"),
                            List.of("tradeable", "playable", "shadeable"),
                            "a fadeable blue, like old ink"));
            default -> List.of(
                    new Word("blackness", "noun", 2, "BLACK-ness", "B L AE1 K N AH0 S",
                            "perfect", "feminine",
                            "The quality of being without light; complete or near-complete darkness.",
                            List.of("gloom", "murk", "pitch", "obscurity"),
                            List.of("starkness", "darkness", "harshness"),
                            "a blackness that swallowed the lamplight"),
                    new Word("vividness", "noun", 3, "VIV-id-ness", "V IH1 V AH0 D N AH0 S",
                            "perfect", "feminine",
                            "Intensity of colour, imagery, or recollection; the quality of being strikingly clear.",
                            List.of("brilliance", "intensity", "clarity", "sharpness"),
                            List.of("liveliness", "timidness", "solidness"),
                            "the vividness of a half-remembered room"),
                    new Word("fortress", "noun", 2, "FOR-tress", "F AO1 R T R AH0 S",
                            "slant", "masculine",
                            "A military stronghold, especially a strongly fortified town.",
                            List.of("citadel", "stronghold", "bastion", "keep"),
                            List.of("buttress", "mattress", "actress"),
                            "a fortress of unlit windows"));
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
            sleep(200);   // pretend we walked a dictionary and a thesaurus

            return "<b>" + word.text() + "</b> &middot; <i>" + word.partOfSpeech() + "</i>\n"
                    + word.syllables() + " syllables &middot; " + word.stress() + "\n"
                    + word.rhymeType() + " rhyme &middot; " + word.ending() + "\n"
                    + "\n"
                    + word.definition() + "\n"
                    + "\n"
                    + "<i>Synonyms</i> &nbsp;" + String.join(", ", word.synonyms()) + "\n"
                    + "<i>Also rhymes</i> &nbsp;" + String.join(", ", word.alsoRhymes()) + "\n"
                    + "\n"
                    + "<i>\u201c" + word.example() + "\u201d</i>\n"
                    + "\n"
                    + "<font color='#999999'>/" + word.arpaBet() + "/</font>";
        }
    }

    /**
     * A dictionary entry as the preview wants to show it. All demo data for
     * now; the real one comes out of the CMU dictionary.
     */
    public record Word(String text,
                       String partOfSpeech,
                       int syllables,
                       String stress,        // FOR-tress
                       String arpaBet,       // F AO1 R T R AH0 S
                       String rhymeType,     // perfect / slant
                       String ending,        // masculine / feminine / dactylic
                       String definition,
                       List<String> synonyms,
                       List<String> alsoRhymes,
                       String example) {

        public String label() {
            return text + " (" + syllables + " syllables"
                    + ("perfect".equals(rhymeType) ? "" : " - " + rhymeType) + ")";
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