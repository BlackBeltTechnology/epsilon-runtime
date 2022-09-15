package hu.blackbelt.epsilon.runtime.execution.impl;

/*-
 * #%L
 * epsilon-runtime-execution
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Map;
import java.util.Set;

public enum LogLevel {
    ERROR, WARN, INFO, DEBUG, TRACE;

    private static Map<LogLevel, Set<LogLevel>> logLevelSetMap = ImmutableMap.<LogLevel, Set<LogLevel>>builder()
            .put(LogLevel.ERROR, ImmutableSet.of(LogLevel.ERROR))
            .put(LogLevel.WARN, ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN))
            .put(LogLevel.INFO, ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO))
            .put(LogLevel.DEBUG, ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO, LogLevel.DEBUG))
            .put(LogLevel.TRACE, ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO, LogLevel.DEBUG, LogLevel.TRACE))
            .build();

    public static Set<LogLevel> getMatchingLogLevels(LogLevel logLevel) {
        return logLevelSetMap.get(logLevel);
    }

    }
