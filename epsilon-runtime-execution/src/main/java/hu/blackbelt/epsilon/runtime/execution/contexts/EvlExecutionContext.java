package hu.blackbelt.epsilon.runtime.execution.contexts;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.execute.UnsatisfiedConstraint;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class EvlExecutionContext extends EolExecutionContext {

    private EvlModule module = new EvlModule();

    private Collection<String> expectedErrors;

    private Collection<String> expectedWarnings;

    private Boolean failed;

    private static final String MODULE_NAME_SEPARATOR = "|";

    @Builder(builderMethodName = "evlExecutionContextBuilder")
    public EvlExecutionContext(String source, List<ProgramParameter> parameters,
                               Collection<String> expectedErrors, Collection<String> expectedWarnings, EvlModule module) {
        super(source, parameters);
        if (expectedErrors != null) {
            this.expectedErrors = Collections.unmodifiableCollection(expectedErrors);
        }
        if (expectedWarnings != null) {
            this.expectedWarnings = Collections.unmodifiableCollection(expectedWarnings);
        }
        if (module != null) {
            this.module = module;
        }
    }

    public IEolModule getModule(Map<Object, Object> context) {
        return module;
    }

    public boolean isOk() {
        return Boolean.FALSE.equals(failed);
    }

    @Override
    public void post(Map<Object, Object> context) {
        if (expectedErrors != null || expectedWarnings != null) {
            // verify expected errors and warnings

            final Set<String> errorsNotFound = expectedErrors != null ? new HashSet<>(expectedErrors) : Collections.emptySet();
            final Set<String> warningsNotFound = expectedWarnings != null ? new HashSet<>(expectedWarnings) : Collections.emptySet();

            final Set<String> unexpectedErrors = new HashSet<>();
            final Set<String> unexpectedWarnings = new HashSet<>();

            final List<UnsatisfiedConstraint> unsatisfiedWarnings = unsatisfiedWarnings();
            final List<UnsatisfiedConstraint> unsatisfiedErrors = unsatisfiedErrors();

            unsatisfiedWarnings.forEach(e -> {
                final String key = e.getConstraint().getName() + MODULE_NAME_SEPARATOR + e.getMessage();
                if (!warningsNotFound.remove(key) && (expectedWarnings == null || !expectedWarnings.contains(key))) {
                    unexpectedWarnings.add(key);
                }
            });

            unsatisfiedErrors.forEach(e -> {
                final String key = e.getConstraint().getName() + MODULE_NAME_SEPARATOR + e.getMessage();
                if (!errorsNotFound.remove(key) && (expectedErrors == null || !expectedErrors.contains(key))) {
                    unexpectedErrors.add(key);
                }
            });

            failed = !errorsNotFound.isEmpty() || !warningsNotFound.isEmpty() || !unexpectedErrors.isEmpty() || !unexpectedWarnings.isEmpty();

            if (failed) {
                log.error("EVL verification failed");
                log.error("  - errors not found: {}", errorsNotFound);
                log.error("  - warnings not found: {}", warningsNotFound);
                log.error("  - unexpected errors: {}", unexpectedErrors);
                log.error("  - unexpected warnings: {}", unexpectedWarnings);
            } else if (!unsatisfiedErrors.isEmpty()) {
                log.warn("Errors found but ignored because expected error/warning list is set");
            }
        } else {
            failed = !unsatisfiedErrors().isEmpty();
        }
    }

    private List<UnsatisfiedConstraint> unsatisfiedWarnings() {
        return module.getContext().getUnsatisfiedConstraints().stream()
                .filter((uc) -> uc.getConstraint().isCritique())
                .collect(Collectors.toList());
    }

    private List<UnsatisfiedConstraint> unsatisfiedErrors() {
        return module.getContext().getUnsatisfiedConstraints().stream()
                .filter((uc) -> !uc.getConstraint().isCritique())
                .collect(Collectors.toList());
    }

    public String toString() {
        Collection<UnsatisfiedConstraint> unsatisfied = module.getContext().getUnsatisfiedConstraints();

        StringBuffer stringBuffer = new StringBuffer();

        if (unsatisfied.size() > 0) {
            printErrors(stringBuffer);
            printWarnings(stringBuffer);
        } else {
            stringBuffer.append("All constraints have been satisfied");
        }
        return stringBuffer.toString();

    }

    private void printErrors(StringBuffer stringBuffer) {
        stringBuffer.append(unsatisfiedErrors().size() + " error(s) \n");
        for (UnsatisfiedConstraint uc : unsatisfiedErrors()) {
            stringBuffer.append(uc.getMessage() + "\n");
        }
    }

    private void printWarnings(StringBuffer stringBuffer) {
        stringBuffer.append(unsatisfiedWarnings().size() + " warning(s) \n");
        for (UnsatisfiedConstraint uc : unsatisfiedWarnings()) {
            stringBuffer.append(uc.getMessage() + "\n");
        }
    }
}
