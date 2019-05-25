package hu.blackbelt.epsilon.runtime.execution.impl;

import com.google.common.collect.ImmutableSet;
import hu.blackbelt.epsilon.runtime.execution.api.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

public class StringBuilderLogger implements Log {
    public static final String LF = "\n";
    StringBuilder buffer = new StringBuilder();

    public enum LogLevel {
        ERROR, WARN, INFO, DEBUG, TRACE
    }

    private Set<LogLevel> currentLevels = ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO);

    public StringBuilderLogger(LogLevel logLevel) {
        if (logLevel == LogLevel.ERROR) {
            currentLevels = ImmutableSet.of(LogLevel.ERROR);
        } else if (logLevel == LogLevel.WARN) {
            currentLevels = ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN);
        } else if (logLevel == LogLevel.INFO) {
            currentLevels = ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO);
        } else if (logLevel == LogLevel.DEBUG) {
            currentLevels = ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO, LogLevel.DEBUG);
        } else if (logLevel == LogLevel.TRACE) {
            currentLevels = ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO, LogLevel.DEBUG, LogLevel.TRACE);
        }
    }

    public void debug(CharSequence charSequence) {
        if (currentLevels.contains(LogLevel.DEBUG)) {
            buffer.append(LF + charSequence.toString());
        }
    }

    public void debug(CharSequence charSequence, Throwable throwable) {
        if (currentLevels.contains(LogLevel.DEBUG)) {
            buffer.append(LF + charSequence.toString());
            buffer.append(LF + exceptionToString(throwable));
        }
    }

    public void debug(Throwable throwable) {
        if (currentLevels.contains(LogLevel.DEBUG)) {
            buffer.append(LF + exceptionToString(throwable));
        }
    }

    public void info(CharSequence charSequence) {
        if (currentLevels.contains(LogLevel.INFO)) {
            buffer.append(LF + charSequence.toString());
        }
    }

    public void info(CharSequence charSequence, Throwable throwable) {
        if (currentLevels.contains(LogLevel.INFO)) {
            buffer.append(LF + charSequence.toString());
            buffer.append(LF + exceptionToString(throwable));
        }
    }

    public void info(Throwable throwable) {
        if (currentLevels.contains(LogLevel.INFO)) {
            buffer.append(LF + exceptionToString(throwable));
        }
    }

    public void warn(CharSequence charSequence) {
        if (currentLevels.contains(LogLevel.WARN)) {
            buffer.append(LF + charSequence.toString());
        }
    }

    public void warn(CharSequence charSequence, Throwable throwable) {
        if (currentLevels.contains(LogLevel.WARN)) {
            buffer.append(LF + charSequence.toString());
            buffer.append(LF + exceptionToString(throwable));
        }
    }

    public void warn(Throwable throwable) {
        if (currentLevels.contains(LogLevel.WARN)) {
            buffer.append(LF + exceptionToString(throwable));
        }
    }

    public void error(CharSequence charSequence) {
        if (currentLevels.contains(LogLevel.ERROR)) {
            buffer.append(LF + charSequence.toString());
        }
    }

    public void error(CharSequence charSequence, Throwable throwable) {
        if (currentLevels.contains(LogLevel.ERROR)) {
            buffer.append(LF + charSequence.toString());
            buffer.append(LF + exceptionToString(throwable));
        }
    }

    public void error(Throwable throwable) {
        if (currentLevels.contains(LogLevel.ERROR)) {
            buffer.append(LF + exceptionToString(throwable));
        }
    }

    private String exceptionToString(Throwable exception) {
        StringWriter errors = new StringWriter();
        exception.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    public String getBuffer() {
        buffer.append(LF);
        return buffer.toString();
    }
}
