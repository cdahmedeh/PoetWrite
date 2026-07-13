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
 * Not in use yet. Currently, application handler has a close() method that
 * waits until the task bus is done doing all of its work.
 *
 * I tried putting this into the TaskBus, but it's casuing a cycling problem.
 *
 * TODO: Try to move into TaskBus.
 * TODO: See if it can be used to show some kind of indication that a close was
 *       requested so the user isn't confused about why the application didn't
 *       close instantly.
 */
public class ApplicationClosedEvent extends AppEvent {
}
