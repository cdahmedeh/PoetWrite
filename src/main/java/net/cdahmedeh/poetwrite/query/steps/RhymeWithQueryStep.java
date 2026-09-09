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
import net.cdahmedeh.poetwrite.lib.analysis.PhonemeAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Phoneme;
import net.cdahmedeh.poetwrite.query.interfaces.*;
import net.cdahmedeh.poetwrite.service.analyzer.DefinitionAnalyzer;
import net.cdahmedeh.poetwrite.service.analyzer.PhonemeAnalyzer;
import net.cdahmedeh.poetwrite.service.analyzer.RhymingWordsAnalyzer;
import net.cdahmedeh.poetwrite.service.analyzer.SynonymAnalyzer;
import net.cdahmedeh.poetwrite.service.analyzer.SyllableAnalyzer;
import net.cdahmedeh.poetwrite.ui.constant.IconConstants;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Singleton
public class RhymeWithQueryStep extends QueryStep {

    // Everything the candidate preview needs, injected like every other
    // service in the app. The preview methods below just call these straight
    // out. No registry, no indirection, the caching and the blocking rules
    // already handle it.
    private final SyllableAnalyzer syllableAnalyzer;
    private final PhonemeAnalyzer phonemeAnalyzer;
    private final DefinitionAnalyzer definitionAnalyzer;
    private final SynonymAnalyzer synonymAnalyzer;
    private final RhymingWordsAnalyzer rhymingWordsAnalyzer;

    @Inject
    public RhymeWithQueryStep(
            SyllableAnalyzer syllableAnalyzer,
            PhonemeAnalyzer phonemeAnalyzer,
            DefinitionAnalyzer definitionAnalyzer,
            SynonymAnalyzer synonymAnalyzer,
            RhymingWordsAnalyzer rhymingWordsAnalyzer) {
        super("rhyme with");
        icon(IconConstants.RHYME_ICON_PATH);
        this.syllableAnalyzer = syllableAnalyzer;
        this.phonemeAnalyzer = phonemeAnalyzer;
        this.definitionAnalyzer = definitionAnalyzer;
        this.synonymAnalyzer = synonymAnalyzer;
        this.rhymingWordsAnalyzer = rhymingWordsAnalyzer;
    }

    public QueryStep steps() {
        return steps(() -> List.of(
                step("previous line")
                        .preview(s -> line(-1))
                        .steps(relationshipsSteps()),
                step("next line")
                        .preview(s -> line(+1))
                        .steps(relationshipsSteps()),
                step("matching pattern")
                        .preview(RhymeWithQueryStep::patternGroupPrompt)
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
                // Captures the pattern instead of digging it back out of the
                // step. This step IS the group, it already knows.
                //
                // Going through the parameter was the bug. PatternGroupParameter
                // only gets put when the command below runs, and that's on
                // commit. Previews render on highlight. So every group was
                // showing the parent's "pick a group" prompt right up until
                // you'd already picked one.
                .preview(s -> patternGroupText(pattern))
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
            net.cdahmedeh.poetwrite.lib.domain.Word entity =
                    new net.cdahmedeh.poetwrite.lib.domain.Word(word.text());

            // Every preview(..) here is one piece of the box and one task.
            // The first two came back with this lookup so they show up
            // instantly. The rest are separate analyzer calls and land
            // whenever they land.
            steps.add(step(word.label())
                    .preview(s -> heading(word))
                    .preview(s -> word.rhymeType() + " rhyme &middot; " + word.ending())
                    .preview("Syllables", s -> syllables(entity))
                    .preview("Pronunciation", s -> pronunciation(entity))
                    .preview("Definition", s -> definition(entity))
                    .preview("Synonyms", s -> synonyms(entity))
                    .preview("Also rhymes", s -> rhymingWords(entity))
                    .preview(s -> "<i>\u201c" + word.example() + "\u201d</i>"));
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
    //
    // One method per piece of a preview. All BLOCKING, and the controller puts
    // each one on the TaskBus separately, so the ones that already have an
    // answer show up while the slow ones are still going.
    //
    // Nothing special about the analyzer calls below, they go through
    // AnalysisCache like everywhere else. Which means arrowing back onto a
    // candidate you already looked at draws every piece at once, since all
    // five are sitting in the cache against that Word.

    // Hardcoded stand-in for the real poem lines.
    private static final List<String> LINES = List.of(
            "The night was long and starless",
            "A quiet, creeping darkness",
            "And nothing moved at all");

    private static String line(int offset) {
        int index = 1 + offset;
        if (index < 0 || index >= LINES.size()) {
            return "(no line)";
        }
        return LINES.get(index);
    }

    // For "matching pattern" itself, which doesn't have a group of its own.
    // Nothing is picked while it's highlighted, so it just prompts. It still
    // reads the parameter though, because coming BACK to this pane after
    // picking a group is a real thing, and then there is something to show.
    private static String patternGroupPrompt(QueryStep step) {
        PatternGroupParameter group = (PatternGroupParameter)
                step.getParameters().get(PatternGroupParameter.class);

        if (group == null) {
            return "Pick a rhyme group from the poem.";
        }

        return patternGroupText(group.getPattern());
    }

    // The lines already in that group. Takes the pattern instead of the step
    // so a caller that knows its own group can just hand it over.
    private static String patternGroupText(String pattern) {
        return switch (pattern) {
            case "A (less)" -> "<b>A (less)</b><br><br>starless<br>nameless";
            case "B (tion)" -> "<b>B (tion)</b><br><br>attention<br>devotion";
            default -> "<b>C (able)</b><br><br>unspeakable<br>fadeable";
        };
    }

    // Came back with the lookup that built the step, so there's nothing to do
    // here. Keep in mind the rhyme type is relative to whatever we're rhyming
    // against, so it belongs to the lookup and not to the word itself.
    private static String heading(Word candidate) {
        return "<b>" + candidate.text() + "</b> &middot; <i>" + candidate.partOfSpeech() + "</i>";
    }

    private String syllables(net.cdahmedeh.poetwrite.lib.domain.Word word) {
        return syllableAnalyzer.get(word).getNumberOfSyllables() + " syllables";
    }

    private String pronunciation(net.cdahmedeh.poetwrite.lib.domain.Word word) {
        List<Phoneme> phonemes = phonemeAnalyzer.get(word).getPhonemes();

        if (phonemes == null || phonemes.isEmpty()) {
            return null;    // nothing to say, the piece gets dropped
        }

        StringBuilder arpabet = new StringBuilder();
        for (Phoneme phoneme : phonemes) {
            if (arpabet.length() > 0) {
                arpabet.append(' ');
            }
            arpabet.append(phoneme.getPhone());
        }
        return "<font color='#999999'>/" + arpabet + "/</font>";
    }

    private String definition(net.cdahmedeh.poetwrite.lib.domain.Word word) {
        return definitionAnalyzer.get(word).getDefinition();
    }

    private String synonyms(net.cdahmedeh.poetwrite.lib.domain.Word word) {
        return String.join(", ", synonymAnalyzer.get(word).getSynonyms());
    }

    private String rhymingWords(net.cdahmedeh.poetwrite.lib.domain.Word word) {
        return String.join(", ", rhymingWordsAnalyzer.get(word).getRhymingWords());
    }

    /**
     * A dictionary entry as the preview wants to show it.
     *
     * NOTE: Several of these fields are now dead weight. The definition, the
     *       synonyms and the rhyme list moved out to their own analyses, so
     *       the demo data here duplicates what the demo analyzers hold. Left
     *       alone rather than trimmed because the real lookup will return
     *       something much thinner than this anyway, probably little more than
     *       the word and how well it rhymes, with everything else asked for
     *       per row.
     * All demo data for
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