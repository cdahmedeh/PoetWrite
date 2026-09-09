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

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * "remaining meter" branch. Entirely hard-coded for now -- the real version
 * asks the meter analyser what the current line still has room for.
 */
@Singleton
public class MeterQueryStep extends QueryStep {

    @Inject
    public MeterQueryStep() {
        super("remaining meter");
        icon(IconConstants.METER_ICON_PATH);
    }

    public QueryStep steps() {
        return steps(() -> List.of(
                meter("iambic pentameter",
                        "da DUM da DUM da DUM da DUM da DUM",
                        "2 feet remaining on this line"),
                meter("trochaic tetrameter",
                        "DUM da DUM da DUM da DUM da",
                        "1 foot remaining on this line"),
                meter("anapestic trimeter",
                        "da da DUM da da DUM da da DUM",
                        "line is complete"),
                meter("free verse",
                        "no fixed pattern",
                        "nothing to match")
        ));
    }

    /**
     * All three pieces are already known, so nothing here waits on anything.
     * Not every preview needs a lookup behind it.
     */
    private QueryStep meter(String title, String scansion, String remaining) {
        return step(title)
                .preview(s -> "<b>" + title + "</b>")
                .preview(s -> scansion)
                .preview(s -> remaining);
    }
}
