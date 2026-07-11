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
import lombok.Setter;
import net.cdahmedeh.poetwrite.ui.app.PersistenceManager;

//@NoArgsConstructor
public class ContentChangedEvent extends AppEvent {
    @Getter @Setter
    private String content = "";

    @Getter @Setter
    private boolean changed = true;

    public ContentChangedEvent(){
        System.out.println("ContentChangedEvent");
    }

    private PersistenceManager.FileStatus status = PersistenceManager.FileStatus.UNKNOWN;

    public PersistenceManager.FileStatus getStatus() {
        return status;
    }

    public void setStatus(PersistenceManager.FileStatus status) {
        this.status = status;
    }
}
