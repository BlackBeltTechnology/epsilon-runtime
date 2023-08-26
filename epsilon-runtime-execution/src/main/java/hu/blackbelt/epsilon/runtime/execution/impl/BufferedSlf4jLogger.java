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

import com.google.common.collect.ImmutableSet;
import org.slf4j.Logger;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Slf4j
public class BufferedSlf4jLogger implements Logger, Closeable {

    Collection<LogEntry> logEntries;
    Logger logger;

    public static class LogEntry {
        LogLevel logLevel;
        String message;
        Throwable throwable;

        public LogEntry(LogLevel logLevel, String message, Throwable throwable) {
            this.logLevel = logLevel;
            this.message = message;
            this.throwable = throwable;
        }

        public void sendToLogger(Logger logger) {
            if (logLevel == LogLevel.ERROR) {
                logger.error(message, throwable);
            } else if (logLevel == LogLevel.WARN) {
                logger.warn(message, throwable);
            } else if (logLevel == LogLevel.INFO) {
                logger.info(message, throwable);
            } else if (logLevel == LogLevel.DEBUG) {
                logger.debug(message, throwable);
            } else if (logLevel == LogLevel.TRACE) {
                logger.trace(message, throwable);
            }
        }
    }


    private Set<LogLevel> currentLevels = ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO);

    public BufferedSlf4jLogger(Logger logger) {
        logEntries = new ArrayList<>();
        LogLevel logLevel = LogLevel.determinateLogLevel(logger);
        this.logger = logger;
        setLoglevels(logLevel);
    }

    public BufferedSlf4jLogger(LogLevel logLevel) {
        logEntries = new ArrayList<>();
        this.logger = log;
        setLoglevels(logLevel);
    }

    private void setLoglevels(LogLevel logLevel) {
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

    @Override
    public String getName() {
        return "Buffered Slf4j Logger";
    }

    @Override
    public boolean isTraceEnabled() {
        return currentLevels.contains(LogLevel.TRACE);
    }

    @Override
    public void trace(String charSequence) {
        if (isTraceEnabled()) {
            logEntries.add(new LogEntry(LogLevel.TRACE, charSequence.toString(), null));
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (isTraceEnabled()) {
            logEntries.add(new LogEntry(LogLevel.TRACE, String.format(format, arg), null));
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (isTraceEnabled()) {
            logEntries.add(new LogEntry(LogLevel.TRACE, String.format(format, arg1, arg2), null));
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (isTraceEnabled()) {
            logEntries.add(new LogEntry(LogLevel.TRACE, String.format(format, arguments), null));
        }
    }

    @Override
    public void trace(String charSequence, Throwable throwable) {
        if (isTraceEnabled()) {
            logEntries.add(new LogEntry(LogLevel.TRACE, charSequence.toString(), throwable));
        }
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return currentLevels.contains(LogLevel.TRACE);
    }

    @Override
    public void trace(Marker marker, String msg) {
        if (isTraceEnabled()) {
            logEntries.add(new LogEntry(LogLevel.TRACE, msg, null));
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (isTraceEnabled()) {
            logEntries.add(new LogEntry(LogLevel.TRACE, String.format(format, arg), null));
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (isTraceEnabled()) {
            logEntries.add(new LogEntry(LogLevel.TRACE, String.format(format, arg1, arg2), null));
        }
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        if (isTraceEnabled()) {
            logEntries.add(new LogEntry(LogLevel.TRACE, String.format(format, argArray), null));
        }
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (isTraceEnabled()) {
            logEntries.add(new LogEntry(LogLevel.TRACE, msg, t));
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return currentLevels.contains(LogLevel.DEBUG);
    }

    @Override
    public void debug(String charSequence) {
        if (currentLevels.contains(LogLevel.DEBUG)) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, charSequence.toString(), null));
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (isDebugEnabled()) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, String.format(format, arg), null));
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (isDebugEnabled()) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, String.format(format, arg1, arg2), null));
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (isDebugEnabled()) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, String.format(format, arguments), null));
        }
    }

    @Override
    public void debug(String charSequence, Throwable throwable) {
        if (currentLevels.contains(LogLevel.DEBUG)) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, charSequence.toString(), throwable));
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return currentLevels.contains(LogLevel.DEBUG);
    }

    @Override
    public void debug(Marker marker, String msg) {
        if (isDebugEnabled()) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, msg, null));
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (isDebugEnabled()) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, String.format(format, arg), null));
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (isDebugEnabled()) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, String.format(format, arg1, arg2), null));
        }
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        if (isDebugEnabled()) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, String.format(format, arguments), null));
        }
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (isDebugEnabled()) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, msg, t));
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return currentLevels.contains(LogLevel.INFO);
    }

    @Override
    public void info(String charSequence) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.INFO, charSequence.toString(), null));
        }
    }

    @Override
    public void info(String format, Object arg) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.INFO, String.format(format, arg), null));
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.INFO, String.format(format, arg1, arg2), null));
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.INFO, String.format(format, arguments), null));
        }
    }

    @Override
    public void info(String charSequence, Throwable throwable) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.INFO, charSequence.toString(), throwable));
        }
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return currentLevels.contains(LogLevel.INFO);
    }

    @Override
    public void info(Marker marker, String msg) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.INFO, msg, null));
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.INFO, String.format(format, arg), null));
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.INFO, String.format(format, arg1, arg2), null));
        }
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.INFO, String.format(format, arguments), null));
        }
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.INFO, msg, t));
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return currentLevels.contains(LogLevel.WARN);
    }

    @Override
    public void warn(String charSequence) {
        if (isWarnEnabled()) {
            logEntries.add(new LogEntry(LogLevel.WARN, charSequence.toString(), null));
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (isWarnEnabled()) {
            logEntries.add(new LogEntry(LogLevel.WARN, String.format(format, arg), null));
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.WARN, String.format(format, arguments), null));
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (isInfoEnabled()) {
            logEntries.add(new LogEntry(LogLevel.WARN, String.format(format, arg1, arg2), null));
        }
    }

    @Override
    public void warn(String charSequence, Throwable throwable) {
        if (isWarnEnabled()) {
            logEntries.add(new LogEntry(LogLevel.WARN, charSequence.toString(), throwable));
        }
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return currentLevels.contains(LogLevel.WARN);
    }

    @Override
    public void warn(Marker marker, String msg) {
        if (isWarnEnabled()) {
            logEntries.add(new LogEntry(LogLevel.WARN, msg, null));
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if (isWarnEnabled()) {
            logEntries.add(new LogEntry(LogLevel.WARN, String.format(format, arg), null));
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (isWarnEnabled()) {
            logEntries.add(new LogEntry(LogLevel.WARN, String.format(format, arg1, arg2), null));
        }
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        if (isWarnEnabled()) {
            logEntries.add(new LogEntry(LogLevel.WARN, String.format(format, arguments), null));
        }
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if (isWarnEnabled()) {
            logEntries.add(new LogEntry(LogLevel.WARN, msg, t));
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return currentLevels.contains(LogLevel.ERROR);
    }

    @Override
    public void error(String charSequence) {
        if (isErrorEnabled()) {
            logEntries.add(new LogEntry(LogLevel.ERROR, charSequence.toString(), null));
        }
    }

    @Override
    public void error(String format, Object arg) {
        if (isErrorEnabled()) {
            logEntries.add(new LogEntry(LogLevel.ERROR, String.format(format, arg), null));
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (isErrorEnabled()) {
            logEntries.add(new LogEntry(LogLevel.ERROR, String.format(format, arg1, arg2), null));
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (isErrorEnabled()) {
            logEntries.add(new LogEntry(LogLevel.ERROR, String.format(format, arguments), null));
        }
    }

    @Override
    public void error(String charSequence, Throwable throwable) {
        if (isErrorEnabled()) {
            logEntries.add(new LogEntry(LogLevel.ERROR, charSequence.toString(), throwable));
        }
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return currentLevels.contains(LogLevel.ERROR);
    }

    @Override
    public void error(Marker marker, String msg) {
        if (isErrorEnabled()) {
            logEntries.add(new LogEntry(LogLevel.ERROR, msg, null));
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        if (isErrorEnabled()) {
            logEntries.add(new LogEntry(LogLevel.ERROR, String.format(format, arg), null));
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (isErrorEnabled()) {
            logEntries.add(new LogEntry(LogLevel.ERROR, String.format(format, arg1, arg2), null));
        }
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        if (isErrorEnabled()) {
            logEntries.add(new LogEntry(LogLevel.ERROR, String.format(format, arguments), null));
        }
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if (isErrorEnabled()) {
            logEntries.add(new LogEntry(LogLevel.ERROR, msg, t));
        }
    }

    public synchronized void flush() {
        sendToLogger();
        logEntries.clear();
    }

    @Override
    public String toString() {
        return "BufferedSlf4jLogger{logger=" + logger.getName() + "}";
    }

    public void sendToLogger(final Logger logger) {
        logEntries.forEach(e -> e.sendToLogger(logger));
    }

    public void sendToLogger() {
        Logger loggerF = this.logger == null ? log : this.logger;
        logEntries.forEach(e -> e.sendToLogger(loggerF));
    }

    @Override
    public void close() throws IOException {
        this.flush();
    }
    
}
