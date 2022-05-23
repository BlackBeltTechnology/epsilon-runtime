package hu.blackbelt.epsilon.runtime.execution.impl;

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
