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

/**
 * See ./docs/async-design.md for design overview.
 *
 * Basically a notification that can be used by the UI layer to know when a
 * computation is complete and get its result. AppEvent contains no
 * special features of any kind. Extended classes contain the results in any
 * way that is appropriate for the computation.
 *
 * Keep in mind that both the ViewController and services within the tasks can
 * have a say of what it's in it.
 */
public abstract class AppEvent {
}
