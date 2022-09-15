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
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Slf4j
public class BufferedSlf4jLogger implements Log, Closeable {

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
        LogLevel logLevel = Slf4jLog.determinateLogLevel(logger);
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

    public void trace(CharSequence charSequence) {
        if (currentLevels.contains(LogLevel.TRACE)) {
            logEntries.add(new LogEntry(LogLevel.TRACE, charSequence.toString(), null));
        }
    }

    public void trace(CharSequence charSequence, Throwable throwable) {
        if (currentLevels.contains(LogLevel.TRACE)) {
            logEntries.add(new LogEntry(LogLevel.TRACE, charSequence.toString(), throwable));
        }
    }

    public void trace(Throwable throwable) {
        if (currentLevels.contains(LogLevel.TRACE)) {
            logEntries.add(new LogEntry(LogLevel.TRACE, null, throwable));
        }
    }

    public void debug(CharSequence charSequence) {
        if (currentLevels.contains(LogLevel.DEBUG)) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, charSequence.toString(), null));
        }
    }

    public void debug(CharSequence charSequence, Throwable throwable) {
        if (currentLevels.contains(LogLevel.DEBUG)) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, charSequence.toString(), throwable));
        }
    }

    public void debug(Throwable throwable) {
        if (currentLevels.contains(LogLevel.DEBUG)) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, null, throwable));
        }
    }

    public void info(CharSequence charSequence) {
        if (currentLevels.contains(LogLevel.INFO)) {
            logEntries.add(new LogEntry(LogLevel.INFO, charSequence.toString(), null));
        }
    }

    public void info(CharSequence charSequence, Throwable throwable) {
        if (currentLevels.contains(LogLevel.INFO)) {
            logEntries.add(new LogEntry(LogLevel.INFO, charSequence.toString(), throwable));
        }
    }

    public void info(Throwable throwable) {
        if (currentLevels.contains(LogLevel.INFO)) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, null, throwable));
        }
    }

    public void warn(CharSequence charSequence) {
        if (currentLevels.contains(LogLevel.WARN)) {
            logEntries.add(new LogEntry(LogLevel.WARN, charSequence.toString(), null));
        }
    }

    public void warn(CharSequence charSequence, Throwable throwable) {
        if (currentLevels.contains(LogLevel.WARN)) {
            logEntries.add(new LogEntry(LogLevel.WARN, charSequence.toString(), throwable));
        }
    }

    public void warn(Throwable throwable) {
        if (currentLevels.contains(LogLevel.WARN)) {
            logEntries.add(new LogEntry(LogLevel.WARN, null, throwable));
        }
    }

    public void error(CharSequence charSequence) {
        if (currentLevels.contains(LogLevel.ERROR)) {
            logEntries.add(new LogEntry(LogLevel.ERROR, charSequence.toString(), null));
        }
    }

    public void error(CharSequence charSequence, Throwable throwable) {
        if (currentLevels.contains(LogLevel.ERROR)) {
            logEntries.add(new LogEntry(LogLevel.ERROR, charSequence.toString(), throwable));
        }
    }

    public void error(Throwable throwable) {
        if (currentLevels.contains(LogLevel.ERROR)) {
            logEntries.add(new LogEntry(LogLevel.DEBUG, null, throwable));
        }
    }

    @Override
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
