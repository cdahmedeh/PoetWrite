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

package net.cdahmedeh.poetwrite.component;

import dagger.Component;
import net.cdahmedeh.poetwrite.service.analyzer.LineAnalyzer;
import net.cdahmedeh.poetwrite.service.analyzer.RhymeAnalyzer;
import net.cdahmedeh.poetwrite.service.analyzer.SyllableAnalyzer;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import javax.inject.Singleton;

@Component
@Singleton
public interface TestComponent {
    TaskBus getTaskBus();

    RhymeAnalyzer getRhymeAnalyzer();
    SyllableAnalyzer getSyllableAnalyzer();
    LineAnalyzer getLineAnalyzer();
}
