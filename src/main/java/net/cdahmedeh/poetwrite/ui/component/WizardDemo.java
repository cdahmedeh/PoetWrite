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

package net.cdahmedeh.poetwrite.ui.component;

import net.cdahmedeh.poetwrite.ui.constant.IconConstants;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Throwaway demo of the mockup's flow, standalone from the editor.
 * Delete once the real steps are wired to LineAnalyzer / the dictionary.
 *
 * Run: Rhyme with… → next line… → means… → type "darkness" → pick a candidate.
 * Completion prints the chain, e.g.
 *   [Action[name=rhyme], Target[name=next-line], Relation[name=means, argument=darkness], Candidate[word=fortress]]
 */
public final class WizardDemo {

    // The clause vocabulary — the chain of these IS the query.
    record Action(String name) {}
    record Target(String name) {}
    record Relation(String name, String argument) {}
    record Candidate(String word) {}

    /** Stands in for the real, presumably-slower LineAnalyzer/TaskBus lookup. */
    private static final int SIMULATED_LOOKUP_DELAY_MS = 20;

    private static final ScheduledExecutorService DELAY_EXECUTOR =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "wizard-demo-lookup");
                t.setDaemon(true); // never keeps the demo JVM alive on its own
                return t;
            });

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WizardDemo::show);
    }

    private static void show() {
        JFrame frame = new JFrame("AutocompleteWizard demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        AutoCompleteWizard wizard = new AutoCompleteWizard(actionStep(), new AutoCompleteWizard.Listener() {
            @Override public void completed(WizardChain chain) {
                System.out.println("QUERY: " + chain.values());
                frame.dispose();
            }
            @Override public void cancelled() {
                System.out.println("cancelled");
                frame.dispose();
            }
            @Override public void layoutChanged() {
                frame.pack();
            }
        });

        frame.add(wizard);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        wizard.focusActivePane();
    }

    // ---------------------------------------------------------------- step 1

    private static WizardStep actionStep() {
        return (chain, typed) -> List.of(
                WizardOption.next("Rhyme with …", new Action("rhyme"), c -> targetStep()).icon(IconConstants.RHYME_ICON_PATH),
                WizardOption.next("Remaining meter …", new Action("meter"), c -> targetStep()).icon(IconConstants.METER_ICON_PATH),
                WizardOption.separator(),
                WizardOption.terminal("Definitions …", new Action("define")).icon(IconConstants.DICTIONARY_ICON_PATH),
                WizardOption.terminal("Relationships …", new Action("relations")).icon(IconConstants.RELATIONSHIPS_ICON_PATH));
    }

    // ---------------------------------------------------------------- step 2

    private static WizardStep targetStep() {
        return (chain, typed) -> List.of(
                WizardOption.next("… previous line …", new Target("previous-line"),
                        () -> preview("previous line", "…are aery with insipid harshness."),
                        c -> relationStep()),
                WizardOption.next("… next line …", new Target("next-line"),
                        () -> preview("next line", "…while my consciousness"),
                        c -> relationStep()),
                WizardOption.next("… matching pattern …", new Target("pattern"), c -> relationStep()),
                WizardOption.next("… clipboard …", new Target("clipboard"), c -> relationStep()));
    }

    // ---------------------------------------------------------------- step 3

    private static WizardStep relationStep() {
        return (chain, typed) -> List.of(
                WizardOption.next("… means …", null, c -> seedStep("means")),
                WizardOption.next("… related to …", null, c -> seedStep("related-to")),
                WizardOption.next("… sounds like …", null, c -> seedStep("sounds-like")));
    }

    // ------------------------------------------------- step 4: field + list

    /**
     * Combined pane: the user types the seed word, the list below regenerates
     * per keystroke. The Relation clause captures the typed text, so the seed
     * ends up in the query via the committed candidate's step context.
     */
    private static WizardStep seedStep(String relationName) {
        return new WizardStep() {
            @Override public boolean hasTextField() { return true; }
            @Override public String placeholder()   { return "word…"; }

            // Unused directly — optionsAsync below is what actually runs. Kept
            // trivial since WizardStep requires it (it's the interface's sole
            // abstract method, for lambda-friendliness elsewhere in this file).
            @Override public List<WizardOption> options(WizardChain chain, String typed) {
                return List.of();
            }

            @Override
            public void optionsAsync(WizardChain chain, String typed,
                                     java.util.function.Consumer<List<WizardOption>> callback) {
                if (typed.isBlank()) {
                    callback.accept(List.of()); // nothing to look up — answer immediately
                    return;
                }
                Relation relation = new Relation(relationName, typed.toLowerCase(Locale.ROOT));
                // Real version: LineAnalyzer / dictionary lookup (via TaskBus).
                // Simulated here with a fixed delay on a background thread so the
                // wizard's async/stale-result handling has something real to do.
                DELAY_EXECUTOR.schedule(() -> callback.accept(List.of(
                                candidate(relation, "blackness", "3 syllables",
                                        "The absence of light.", "F AO1 R T R AH0 S"),
                                candidate(relation, "vividness", "3 syllables",
                                        "Intense clarity of imagery.", "V IH1 V AH0 D N AH0 S"),
                                candidate(relation, "fortress", "2 syllables - slant",
                                        "A military stronghold, especially a strongly fortified town.",
                                        "F AO1 R T R AH0 S"))),
                        SIMULATED_LOOKUP_DELAY_MS, TimeUnit.MILLISECONDS);
            }
        };
    }

    private static WizardOption candidate(Relation relation, String word,
                                          String match, String definition, String arpabet) {
        return WizardOption.terminal(
                word + " (" + match + ")",
                new Candidate(word),
                () -> preview(word,
                        "1 syllable match (ess) - masculine rhyme\n\n"
                                + "Definition: " + definition + "\n"
                                + "Relation: " + relation + "\n\n"
                                + "ARPAbet: " + arpabet));
    }

    // --------------------------------------------------------------- preview

    private static JComponent preview(String title, String body) {
        String html = "<html><b>" + title + "</b><br><br>"
                + body.replace("\n", "<br>") + "</html>";
        JLabel label = new JLabel(html);
        label.setVerticalAlignment(JLabel.TOP);
        return label;
    }

    /** Entry point of the demo flow, usable from outside while wiring the real steps. */
    public static WizardStep root() {
        return actionStep();
    }
}