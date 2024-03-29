package hu.blackbelt.epsilon.runtime.execution.contexts;

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

import hu.blackbelt.epsilon.runtime.execution.exceptions.EvlScriptExecutionException;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.evl.EvlModule;
import org.eclipse.epsilon.evl.concurrent.EvlModuleParallelElements;
import org.eclipse.epsilon.evl.execute.UnsatisfiedConstraint;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class EvlExecutionContext extends EolExecutionContext {

    private final EvlModule module;

    private Collection<String> expectedErrors;

    private Collection<String> expectedWarnings;

    private Boolean failed;

    private static final String MODULE_NAME_SEPARATOR = "|";

    @Builder(builderMethodName = "evlExecutionContextBuilder")
    public EvlExecutionContext(URI source, List<ProgramParameter> parameters,
                               Collection<String> expectedErrors, Collection<String> expectedWarnings, EvlModule module,
                               Boolean parallel) {
        super(source, parameters, false, parallel);
        if (expectedErrors != null) {
            this.expectedErrors = Collections.unmodifiableCollection(expectedErrors);
        }
        if (expectedWarnings != null) {
            this.expectedWarnings = Collections.unmodifiableCollection(expectedWarnings);
        }
        if (module != null) {
            this.module = module;
        // TODO: Remove when JNG-3096 Resolved
        } else if (Boolean.getBoolean("disableEpsilonParallel")) {
            this.module = new EvlModule();
        } else if (parallel == null || parallel) {
            this.module = new EvlModuleParallelElements();
        } else {
            this.module = new EvlModule();
        }
    }

    public IEolModule getModule(Map<Object, Object> context) {
        return module;
    }

    public boolean isOk() {
        return Boolean.FALSE.equals(failed);
    }

    @Override
    public void post(Map<Object, Object> context) throws EvlScriptExecutionException {
        if (expectedErrors != null || expectedWarnings != null) {
            // verify expected errors and warnings

            final Set<String> errorsNotFound = expectedErrors != null ? new HashSet<>(expectedErrors) : Collections.emptySet();
            final Set<String> warningsNotFound = expectedWarnings != null ? new HashSet<>(expectedWarnings) : Collections.emptySet();

            final Set<String> unexpectedErrors = new HashSet<>();
            final Set<String> unexpectedWarnings = new HashSet<>();

            final List<UnsatisfiedConstraint> unsatisfiedWarnings = unsatisfiedWarnings();
            final List<UnsatisfiedConstraint> unsatisfiedErrors = unsatisfiedErrors();

            if (expectedWarnings != null) {
                unsatisfiedWarnings.forEach(e -> {
                    final String key = e.getConstraint().getName() + MODULE_NAME_SEPARATOR + e.getMessage();
                    if (!warningsNotFound.remove(key) && (expectedWarnings == null || !expectedWarnings.contains(key))) {
                        unexpectedWarnings.add(key);
                    }
                });
            }

            unsatisfiedErrors.forEach(e -> {
                final String key = e.getConstraint().getName() + MODULE_NAME_SEPARATOR + e.getMessage();
                if (!errorsNotFound.remove(key) && (expectedErrors == null || !expectedErrors.contains(key))) {
                    unexpectedErrors.add(key);
                }
            });

            failed = !errorsNotFound.isEmpty() || !warningsNotFound.isEmpty() || !unexpectedErrors.isEmpty() || !unexpectedWarnings.isEmpty();

            if (failed) {
                throw EvlScriptExecutionException.evlScriptExecutionExceptionBuilder()
                        .message(getSource().toString())
                        .errorsNotFound(errorsNotFound)
                        .warningsNotFound(warningsNotFound)
                        .unexpectedErrors(unexpectedErrors)
                        .unexpectedWarnings(unexpectedWarnings)
                        .unsatisfiedErrors(unsatisfiedErrors())
                        .unsatisfiedWarnings(unsatisfiedWarnings())
                        .build();
            }
        } else {
            failed = !unsatisfiedErrors().isEmpty();
            if (failed) {
                throw EvlScriptExecutionException.evlScriptExecutionExceptionBuilder()
                        .message(getSource().toString())
                        .unsatisfiedErrors(unsatisfiedErrors())
                        .unsatisfiedWarnings(unsatisfiedWarnings())
                        .build();
            }
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
}
