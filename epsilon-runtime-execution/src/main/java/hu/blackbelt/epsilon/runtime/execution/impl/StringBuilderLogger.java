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
import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

public class StringBuilderLogger implements Log {
    public static final String LF = "\n";
    StringBuilder buffer = new StringBuilder();

    Logger logger;

    @Override
    public void close() throws Exception {
        flush();
    }

    private Set<LogLevel> currentLevels = ImmutableSet.of(LogLevel.ERROR, LogLevel.WARN, LogLevel.INFO);

    public StringBuilderLogger() {
    }

    public StringBuilderLogger(Logger logger) {
        LogLevel logLevel = Slf4jLog.determinateLogLevel(logger);
        this.logger = logger;
        currentLevels = LogLevel.getMatchingLogLevels(logLevel);
    }

    public StringBuilderLogger(LogLevel logLevel) {
        currentLevels = LogLevel.getMatchingLogLevels(logLevel);
    }

    public void trace(CharSequence charSequence) {
        if (currentLevels.contains(LogLevel.TRACE)) {
            buffer.append(LF + charSequence.toString());
        }
    }

    public void trace(CharSequence charSequence, Throwable throwable) {
        if (currentLevels.contains(LogLevel.TRACE)) {
            buffer.append(LF + charSequence.toString());
            buffer.append(LF + exceptionToString(throwable));
        }
    }

    public void trace(Throwable throwable) {
        if (currentLevels.contains(LogLevel.TRACE)) {
            buffer.append(LF + exceptionToString(throwable));
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

    @Override
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

}
