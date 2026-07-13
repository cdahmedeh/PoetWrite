/**
 * PoetWrite - A Poetry Writing Application
 * Copyright (C) 2025 Ahmed El-Hajjar
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
import net.cdahmedeh.poetwrite.ui.services.PersistenceManager;

/**
 * Very important event. This is used to notify that the contents have been
 * updated. So someone wrote something in the text editor.
 *
 * It's mostly to make the PersistenceHandler aware that changes were made to
 * the text, and update its own cache so saving is done without ever needing
 * to access the review. It also eventually gets absorbed by the MainViewModel
 * that contains the editor.
 *
 * TODO: I'm planning to have some kind of debounce algorithm for that. I don't
 *       know yet if this would need another kind of event. The issue right now,
 *       is that pretty much any change to the text, even a letter inserted
 *       gets whole round trip from View to TaskBus back to Model.
 * TODO: Also, this could trigger the analysis.
 */
@NoArgsConstructor
public class ContentChangedEvent extends AppEvent {
    @Getter @Setter
    private String content = "";

    @Getter @Setter
    private boolean changed = true;

    @Getter @Setter
    private PersistenceManager.FileStatus status = PersistenceManager.FileStatus.UNKNOWN;
}
