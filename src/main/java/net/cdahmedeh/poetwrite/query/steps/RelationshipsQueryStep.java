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

import net.cdahmedeh.poetwrite.annotation.Draft;
import net.cdahmedeh.poetwrite.annotation.Helped;
import net.cdahmedeh.poetwrite.query.interfaces.*;
import net.cdahmedeh.poetwrite.ui.constant.IconConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * "relationships" branch: pick a relation, then type a word.
 *
 * The relation rows carry icons and the branch itself does not, so this pane
 * exercises the renderer's icon-alignment indent.
 */
@Draft("Currently hard-coded")
@Helped("100% generated")
public class RelationshipsQueryStep extends QueryStep {

    public RelationshipsQueryStep() {
        super("relationships");
        icon(IconConstants.RELATIONSHIPS_ICON_PATH);
    }

    public QueryStep steps() {
        return steps(() -> List.of(
                relation("synonyms", null),
                relation("antonyms", null),
                relation("broader terms", null),
                relation("narrower terms", null)
        ));
    }

    private QueryStep relation(String name, String icon) {
        QueryStep step = step(name)
                .search(QuerySearch::new)
                .command(this::lookup);
        return icon == null ? step : step.icon(icon);
    }

    private List<QueryStep> lookup(QueryStep step) {
        String typed = step.getSearch().getText().trim().toLowerCase();
        if (typed.isEmpty()) {
            return List.of();
        }

        List<QueryStep> steps = new ArrayList<>();
        for (String related : related(step.getName(), typed)) {
            steps.add(step(related).preview(() -> new RelationPreview(step.getName(), typed, related)));
        }
        return steps;
    }

    private List<String> related(String relation, String word) {
        return switch (relation) {
            case "synonyms"       -> List.of("gloom", "murk", "shadow", "obscurity");
            case "antonyms"       -> List.of("light", "brightness", "radiance");
            case "broader terms"  -> List.of("absence", "condition");
            default               -> List.of("dusk", "twilight", "penumbra");
        };
    }

    public static class RelationPreview extends QueryPreview {
        private final String relation;
        private final String source;
        private final String target;

        public RelationPreview(String relation, String source, String target) {
            this.relation = relation;
            this.source = source;
            this.target = target;
        }

        @Override
        public String render(QueryStep step) {
            return "<b>" + target + "</b><br><br>"
                    + source + " &rarr; " + relation + " &rarr; " + target;
        }
    }
}