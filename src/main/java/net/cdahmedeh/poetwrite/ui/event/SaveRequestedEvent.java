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

package net.cdahmedeh.poetwrite.ui.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Called when a user requests to save their poem. What this does is if it
 * checks if a dialog is needed.
 *
 * For example, the dialog is needed when.
 * - A new file was created and the user entered some text. Then you'll want to
 *   ask them to put the new file somewhere.
 * - The user clicked save as.
 *
 * When it shouldn't be
 * - The file has no changes, and click saves, shouldn't be interrupted.
 */

@NoArgsConstructor
public class SaveRequestedEvent extends AppEvent {
    @Getter
    @Setter
    private boolean dialogNeeded = true;
}