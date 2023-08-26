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
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

public class StringBuilderLogger implements Logger, AutoCloseable {
    public static final String LF = "\n";
    StringBuilder buffer = new StringBuilder();

    final Logger logger;

    @Override
    public void close() throws Exception {
        flush();
    }

    private Set<LogLevel> currentLevels = ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO);

    public StringBuilderLogger() {
        this.logger = null;
    }

    public StringBuilderLogger(Logger logger) {
        LogLevel logLevel = LogLevel.determinateLogLevel(logger);
        this.logger = logger;
        currentLevels = LogLevel.getMatchingLogLevels(logLevel);
    }

    public StringBuilderLogger(LogLevel logLevel) {
        currentLevels = LogLevel.getMatchingLogLevels(logLevel);
        this.logger = null;
    }

    @Override
    public void trace(String charSequence) {
        if (isTraceEnabled()) {
            buffer.append(LF + charSequence.toString());
        }
    }

    @Override
    public void trace(String charSequence, Throwable throwable) {
        if (isTraceEnabled()) {
            buffer.append(LF + charSequence.toString());
            buffer.append(LF + exceptionToString(throwable));
        }
    }

    @Override
    public void debug(String charSequence) {
        if (isDebugEnabled()) {
            buffer.append(LF + charSequence.toString());
        }
    }

    @Override
    public void debug(String charSequence, Throwable throwable) {
        if (isDebugEnabled()) {
            buffer.append(LF + charSequence.toString());
            buffer.append(LF + exceptionToString(throwable));
        }
    }

    @Override
    public void info(String charSequence) {
        if (isInfoEnabled()) {
            buffer.append(LF + charSequence.toString());
        }
    }

    @Override
    public void info(String charSequence, Throwable throwable) {
        if (isInfoEnabled()) {
            buffer.append(LF + charSequence.toString());
            buffer.append(LF + exceptionToString(throwable));
        }
    }

    @Override
    public void warn(String charSequence) {
        if (isWarnEnabled()) {
            buffer.append(LF + charSequence.toString());
        }
    }

    @Override
    public void warn(String charSequence, Throwable throwable) {
        if (isWarnEnabled()) {
            buffer.append(LF + charSequence.toString());
            buffer.append(LF + exceptionToString(throwable));
        }
    }

    @Override
    public void error(String charSequence) {
        if (currentLevels.contains(LogLevel.ERROR)) {
            buffer.append(LF + charSequence.toString());
        }
    }

    @Override
    public void error(String charSequence, Throwable throwable) {
        if (currentLevels.contains(LogLevel.ERROR)) {
            buffer.append(LF + charSequence.toString());
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

    public void flush() {
        if (logger != null) {
            String buff = getBuffer();
            if (buff.trim().length() > 0) {
                logger.info(getBuffer());
            }
        }
        buffer = new StringBuilder();
    }

    @Override
    public String toString() {
        return "StringBuilderLogger";
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public boolean isTraceEnabled() {
        return currentLevels.contains(LogLevel.TRACE);
    }

    @Override
    public void trace(String format, Object arg) {
        if (isTraceEnabled()) {
            buffer.append(LF + String.format(format, arg));
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (isTraceEnabled()) {
            buffer.append(LF + String.format(format, arg1, arg2));
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (isTraceEnabled()) {
            buffer.append(LF + String.format(format, arguments));
        }
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return currentLevels.contains(LogLevel.TRACE);
    }

    @Override
    public void trace(Marker marker, String msg) {
        if (isTraceEnabled()) {
            buffer.append(LF + msg);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (isTraceEnabled()) {
            buffer.append(LF + String.format(format, arg));
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (isTraceEnabled()) {
            buffer.append(LF + String.format(format, arg1, arg2));
        }
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        if (isTraceEnabled()) {
            buffer.append(LF + String.format(format, argArray));
        }
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (isTraceEnabled()) {
            buffer.append(LF + msg);
            buffer.append(LF + exceptionToString(t));
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return currentLevels.contains(LogLevel.DEBUG);
    }

    @Override
    public void debug(String format, Object arg) {
        if (isDebugEnabled()) {
            buffer.append(LF + String.format(format, arg));
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (isDebugEnabled()) {
            buffer.append(LF + String.format(format, arg1, arg2));
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (isDebugEnabled()) {
            buffer.append(LF + String.format(format, arguments));
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return currentLevels.contains(LogLevel.DEBUG);
    }

    @Override
    public void debug(Marker marker, String msg) {
        if (isDebugEnabled()) {
            buffer.append(LF + msg);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (isDebugEnabled()) {
            buffer.append(LF + String.format(format, arg));
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (isDebugEnabled()) {
            buffer.append(LF + String.format(format, arg1, arg2));
        }
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        if (isDebugEnabled()) {
            buffer.append(LF + String.format(format, arguments));
        }
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (isDebugEnabled()) {
            buffer.append(LF + msg);
            buffer.append(LF + exceptionToString(t));
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return currentLevels.contains(LogLevel.INFO);
    }


    @Override
    public void info(String format, Object arg) {
        if (isInfoEnabled()) {
            buffer.append(LF + String.format(format, arg));
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (isInfoEnabled()) {
            buffer.append(LF + String.format(format, arg1, arg2));
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (isInfoEnabled()) {
            buffer.append(LF + String.format(format, arguments));
        }
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return currentLevels.contains(LogLevel.INFO);
    }

    @Override
    public void info(Marker marker, String msg) {
        if (isInfoEnabled()) {
            buffer.append(LF + msg);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if (isInfoEnabled()) {
            buffer.append(LF + String.format(format, arg));
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (isInfoEnabled()) {
            buffer.append(LF + String.format(format, arg1, arg2));
        }
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        if (isInfoEnabled()) {
            buffer.append(LF + String.format(format, arguments));
        }
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if (isInfoEnabled()) {
            buffer.append(LF + msg);
            buffer.append(LF + exceptionToString(t));
        }
    }

    @Override
    public boolean isWarnEnabled() {
        return currentLevels.contains(LogLevel.WARN);
    }

    @Override
    public void warn(String format, Object arg) {
        if (isWarnEnabled()) {
            buffer.append(LF + String.format(format, arg));
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (isWarnEnabled()) {
            buffer.append(LF + String.format(format, arguments));
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (isWarnEnabled()) {
            buffer.append(LF + String.format(format, arg1, arg2));
        }
    }


    @Override
    public boolean isWarnEnabled(Marker marker) {
        return currentLevels.contains(LogLevel.WARN);
    }

    @Override
    public void warn(Marker marker, String msg) {
        if (isWarnEnabled()) {
            buffer.append(LF + msg);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if (isWarnEnabled()) {
            buffer.append(LF + String.format(format, arg));
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (isWarnEnabled()) {
            buffer.append(LF + String.format(format, arg1, arg2));
        }
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        if (isWarnEnabled()) {
            buffer.append(LF + String.format(format, arguments));
        }
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if (isWarnEnabled()) {
            buffer.append(LF + msg);
            buffer.append(LF + exceptionToString(t));
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return currentLevels.contains(LogLevel.ERROR);
    }

    @Override
    public void error(String format, Object arg) {
        if (isErrorEnabled()) {
            buffer.append(LF + String.format(format, arg));
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (isErrorEnabled()) {
            buffer.append(LF + String.format(format, arg1, arg2));
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (isErrorEnabled()) {
            buffer.append(LF + String.format(format, arguments));
        }
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return currentLevels.contains(LogLevel.ERROR);
    }

    @Override
    public void error(Marker marker, String msg) {
        if (isErrorEnabled()) {
            buffer.append(LF + msg);
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        if (isErrorEnabled()) {
            buffer.append(LF + String.format(format, arg));
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (isErrorEnabled()) {
            buffer.append(LF + String.format(format, arg1, arg2));
        }
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        if (isErrorEnabled()) {
            buffer.append(LF + String.format(format, arguments));
        }
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if (isErrorEnabled()) {
            buffer.append(LF + msg);
            buffer.append(LF + exceptionToString(t));
        }
    }
}
