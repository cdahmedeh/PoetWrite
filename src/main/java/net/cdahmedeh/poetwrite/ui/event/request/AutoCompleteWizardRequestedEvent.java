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

package net.cdahmedeh.poetwrite.ui.event.request;

import lombok.Getter;
import lombok.Setter;
import net.cdahmedeh.poetwrite.query.interfaces.QueryStep;
import net.cdahmedeh.poetwrite.ui.event.interfaces.AppEvent;

/**
 * Called when the user presses the autocomplete shortcut (Ctrl+Space) to start
 * the process of actually bringing up the wizard.
 *
 * NOTE: Could have been complete bypassed and done in the view enteritis, but
 *       because an extensive amount of functionality will be available, it
 *       might take some time to build the wizard's features.
 *
 */
public class AutoCompleteWizardRequestedEvent extends AppEvent {
    @Getter
    @Setter
    private boolean requested = false;

    @Getter @Setter
    private QueryStep root;
}
