package hu.blackbelt.epsilon.runtime.execution.impl;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public enum LogLevel {
    ERROR, WARN, INFO, DEBUG, TRACE;

    public static Set<LogLevel> getMatchingLogLevels(LogLevel logLevel) {
        Set<LogLevel> levels = new HashSet<>();
        if (logLevel == LogLevel.ERROR) {
            levels = ImmutableSet.of(LogLevel.ERROR);
        } else if (logLevel == LogLevel.WARN) {
            levels = ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN);
        } else if (logLevel == LogLevel.INFO) {
            levels = ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO);
        } else if (logLevel == LogLevel.DEBUG) {
            levels = ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO, LogLevel.DEBUG);
        } else if (logLevel == LogLevel.TRACE) {
            levels = ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO, LogLevel.DEBUG, LogLevel.TRACE);
        }
        return levels;
    }

    }
