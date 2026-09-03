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

import net.cdahmedeh.poetwrite.query.interfaces.*;
import net.cdahmedeh.poetwrite.ui.constant.IconConstants;

import java.util.List;

/**
 * "remaining meter" branch. Entirely hard-coded for now -- the real version
 * asks the meter analyser what the current line still has room for.
 */
public class MeterQueryStep extends QueryStep {

    public MeterQueryStep() {
        super("remaining meter");
        icon(IconConstants.METER_ICON_PATH);
    }

    public QueryStep steps() {
        return steps(() -> List.of(
                step("iambic pentameter").preview(() -> new MeterPreview(
                        "iambic pentameter",
                        "da DUM da DUM da DUM da DUM da DUM",
                        "2 feet remaining on this line")),
                step("trochaic tetrameter").preview(() -> new MeterPreview(
                        "trochaic tetrameter",
                        "DUM da DUM da DUM da DUM da",
                        "1 foot remaining on this line")),
                step("anapestic trimeter").preview(() -> new MeterPreview(
                        "anapestic trimeter",
                        "da da DUM da da DUM da da DUM",
                        "line is complete")),
                step("free verse").preview(() -> new MeterPreview(
                        "free verse",
                        "no fixed pattern",
                        "nothing to match"))
        ));
    }

    public static class MeterPreview extends QueryPreview {
        private final String title;
        private final String scansion;
        private final String remaining;

        public MeterPreview(String title, String scansion, String remaining) {
            this.title = title;
            this.scansion = scansion;
            this.remaining = remaining;
        }

        @Override
        public String render(QueryStep step) {
            return "<b>" + title + "</b><br><br>" + scansion + "<br><br>" + remaining;
        }
    }
}