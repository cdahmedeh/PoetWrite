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

package net.cdahmedeh.poetwrite.ui.constant;

/**
 * UI interaction prompts
 */
public class PromptConstants {
    public static final String PROMPT_UNSAVED_CHANGES_FOR_NEW = "Your poem has some unsaved changes. Are you sure you want to create a new poem and lose your changes?";
    public static final String PROMPT_UNSAVED_CHANGED_FOR_OPEN = "Your poem has some unsaved changes. Are you sure you want to open a new poem and lose your changes?";
    public static final String PROMPT_UNSAVED_CHANGED_FOR_QUIT = "Your poem has some unsaved changes. Are you sure you want to quit PoetWrite and lose your changes?";
    public static final String PROMPT_MESSAGE_OVERWRITE = "The poem (%s) already exists. Are you sure you want to overwrite it?";

    public static final String TITLE_UNSAVED_CHANGES = "Unsaved Changes";
    public static final String TITLE_QUIT = "Quit Confirmation";
    public static final String TITLE_OVERWRITE = "Overwrite Confirmation";
}
