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

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import org.slf4j.Logger;

public class Slf4jLog implements Log {

    private static Logger defaultLogger = org.slf4j.LoggerFactory.getLogger("Epsilon");

    private Logger log;

    public Slf4jLog() {
        this.log = defaultLogger;
    }

    public Slf4jLog(Logger logger) {
        this.log = logger;
    }

    public void trace(CharSequence charSequence) {
        log.trace(charSequence.toString());
    }

    public void trace(CharSequence charSequence, Throwable throwable) {
        log.trace(charSequence.toString(), throwable);
    }

    public void trace(Throwable throwable) {
        log.trace("", throwable);
    }

    public void debug(CharSequence charSequence) {
        log.debug(charSequence.toString());
    }

    public void debug(CharSequence charSequence, Throwable throwable) {
        log.debug(charSequence.toString(), throwable);
    }

    public void debug(Throwable throwable) {
        log.debug("", throwable);
    }

    public void info(CharSequence charSequence) {
        log.info(charSequence.toString());
    }

    public void info(CharSequence charSequence, Throwable throwable) {
        log.info(charSequence.toString(), throwable);
    }

    public void info(Throwable throwable) {
        log.info("", throwable);
    }

    public void warn(CharSequence charSequence) {
        log.warn(charSequence.toString());
    }

    public void warn(CharSequence charSequence, Throwable throwable) {
        log.warn(charSequence.toString(), throwable);
    }

    public void warn(Throwable throwable) {
        log.warn("", throwable);
    }

    public void error(CharSequence charSequence) {
        log.error(charSequence.toString());
    }

    public void error(CharSequence charSequence, Throwable throwable) {
        log.error(charSequence.toString(), throwable);
    }

    public void error(Throwable throwable) {
        log.error("", throwable);
    }

    public static LogLevel determinateLogLevel(Logger log) {
        if (log.isTraceEnabled()) {
            return  LogLevel.TRACE;
        } else if (log.isDebugEnabled()) {
            return  LogLevel.DEBUG;
        } else if (log.isInfoEnabled()) {
            return  LogLevel.INFO;
        } else if (log.isWarnEnabled()) {
            return LogLevel.WARN;
        } else if (log.isErrorEnabled()) {
            return LogLevel.ERROR;
        }
        return  LogLevel.INFO;
    }

    @Override
    public String toString() {
        return "Slf4jLog{name=" + log.getName() +"}";
    }

    @Override
    public void close() throws Exception {
        flush();
    }

    @Override
    public void flush() {
    }

}
