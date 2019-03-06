package hu.blackbelt.epsilon.runtime.execution;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Slf4jLog implements Log {
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
}
