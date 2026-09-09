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

package net.cdahmedeh.poetwrite.query.interfaces;

import lombok.Getter;

/**
 * One piece of what shows up when a step is highlighted in the wizard.
 *
 * A step used to have exactly one preview and it rendered the whole box in one
 * go. Which meant a rhyme candidate couldn't show its definition until the
 * syllable count, the pronunciation, the synonyms and the rhyme list had all
 * finished too. So a step holds a LIST of these now. Each one gets its own
 * task, so the box goes up right away with everything saying Loading, and
 * fills itself in as the answers come back.
 *
 * You never build one of these yourself. QueryStep.preview(..) does it, and
 * calling it more than once is how a step ends up with more than one piece.
 *
 *   step(word.label())
 *       .preview(s -> heading(word))
 *       .preview("Syllables", s -> syllables(word))
 *       .preview("Definition", s -> definition(word))
 *
 * The label is the little grey heading above the text. Leave it out for a
 * plain paragraph, which is what a title line or a closing quote wants.
 *
 * VERY IMPORTANT: Same deal as QueryCommand. The render is BLOCKING and that's
 *                 on purpose. It calls whatever analyzer the step had injected
 *                 straight out, and it's the caller's job to put it on the
 *                 TaskBus and wait for the event. See
 *                 MainViewController.previewQueryStep(..).
 *
 * Identity is left alone on purpose. The wizard uses the object itself to work
 * out which piece a bit of text belongs to, so two pieces that happen to share
 * a label are still two different pieces.
 */
public class QueryPreview {

    /**
     * The part that actually produces the text. Takes the step so a preview
     * can read the parameters collected on the way down, which is how the
     * pattern group preview knows what was picked upstream.
     *
     * BLOCKING.
     */
    @FunctionalInterface
    public interface Render {
        String render(QueryStep step);
    }

    // The little grey heading above the text. Null for a plain paragraph.
    @Getter
    private final String label;

    private final Render render;

    /* package */ QueryPreview(String label, Render render) {
        this.label = label;
        this.render = render;
    }

    /**
     * Produce this piece's text.
     *
     * BLOCKING. Wrap it in a TaskBus task.
     *
     * Returns HTML, so a piece can italicise a part of speech or grey out a
     * pronunciation. Return null or blank when there's nothing to say, and the
     * piece gets left out of the box entirely instead of showing an empty
     * heading.
     */
    public String render(QueryStep step) {
        return render.render(step);
    }
}
