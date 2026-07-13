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

/**
 * Constants for various UI components, mainly for controlling sizes and
 * margins.
 */
package net.cdahmedeh.poetwrite.ui.constant;

public class UIConstants {
    public static final String APP_NAME = "PoetWrite";

    public static final String STRING_FILE = "Poem";
    public static final String STRING_NEW = "New";
    public static final String STRING_OPEN = "Open";
    public static final String STRING_SAVE = "Save";
    public static final String STRING_SAVE_AS = "Save As";
    public static final String STRING_EXIT = "Exit";
    public static final String STRING_TOOLS = "Tools";
    public static final String STRING_GENERATE = "Generate Random Text";

    public static final String STRING_STATUS_DEFAULT = "Ready";
    public static final String STRING_STATUS_COMPLETE = "Done";

    public static final String MESSAGE_OVERWRITE_PROMPT =
            "The poem (%s) already exists. Are you sure you want to overwrite it?";

    public static final String LOG_WELCOME = "Welcome to the world of PoetWrite!";

    public static final String APP_ICON_PATH = "/icons/appicon.png";

    public static final String FILE_ICON_PATH = "/icons/file.svg";
    public static final String NEW_ICON_PATH = "/icons/new.svg";
    public static final String OPEN_ICON_PATH = "/icons/open.svg";
    public static final String SAVE_ICON_PATH = "/icons/save.svg";
    public static final String SAVE_AS_ICON_PATH = "/icons/save-as.svg";
    public static final String EXIT_ICON_PATH = "/icons/exit.svg";

    public static final String TOOLS_ICON_PATH = "/icons/tools.svg";
    public static final String GENERATE_ICON_PATH = "/icons/generate.svg";

    public static final String DEFAULT_EDITOR_FONT = "Noto Sans";
    public static final int DEFAULT_EDITOR_FONT_SIZE = 14;

    public static final int DEFAULT_WINDOW_WIDTH = 1280;
    public static final int DEFAULT_WINDOW_HEIGHT = 800;

    public static final int UI_TITLE_PANE_BUTTON_SIZE_WIDTH = 44;
    public static final int UI_TITLE_PANE_BUTTON_SIZE_HEIGHT = 34;

    public static final int UI_MENU_ITEM_MARGIN_VERTICAL = 6;
    public static final int UI_MENU_ITEM_MARGIN_HORIZONTAL = 8;

    public static final float DEFAULT_LINE_SPACING =  1.10f;

    public static final int SPINNER_STARTING_ANGLE = 0;
    public static final int SPINNER_ANIMATION_INTERVAL = 100;
    public static final int SPINNER_ROTATE_INCREMENTS = 20;
}
