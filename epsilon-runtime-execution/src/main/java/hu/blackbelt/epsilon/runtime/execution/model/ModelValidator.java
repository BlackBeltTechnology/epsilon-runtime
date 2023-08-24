package hu.blackbelt.epsilon.runtime.execution.model;

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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ModelValidationException;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.epsilon.emc.emf.EmfModel;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ModelValidator {

    static {
        EcoreUriFixer.fixEcoreUri();
    }

    public static Diagnostician diagnostician = new Diagnostician();

    public static void validate(EmfModel model) throws ModelValidationException {
        validate(Optional.empty(), model);
    }

    public static void validate(Optional<Logger> log, EmfModel model) throws ModelValidationException {

        List<String> validationErrors = ImmutableList.of();
        validationErrors = ModelValidator.getValidationErrors(log, model);
        if (!validationErrors.isEmpty()) {
            throw new ModelValidationException(validationErrors, model.getResource());
        }

        final List<String> errors = new LinkedList<>();

        model.allContents().forEach(e -> errors.addAll(extractValidationErrors(log, validate(e), 0)));
    }

    public static List<String> getValidationErrors(EmfModel model) {
        return getValidationErrors(Optional.empty(), model);
    }

    public static List<String> getValidationErrors(Optional<Logger> log, EmfModel model) {
        final List<String> errors = new LinkedList<>();

        model.allContents().forEach(e -> errors.addAll(extractValidationErrors(log, validate(e), 0)));

        return errors;
    }

    private static List<String> extractValidationErrors(Optional<Logger> log, Diagnostic diagnostic, int level) {
        final List<String> errors = new LinkedList<>();

        final String message = diagnostic.getMessage();
        final String indent = Strings.padStart("", level * 2, ' ');
        switch (diagnostic.getSeverity()) {
            case Diagnostic.INFO: {
                log.ifPresent(l -> l.info(indent + message));
                break;
            }
            case Diagnostic.WARNING: {
                log.ifPresent(l -> l.warn(indent + message));
                break;
            }
            case Diagnostic.ERROR: {
                log.ifPresent(l -> l.error(indent + message));
                errors.add(message);
                break;
            }
        }

        if (diagnostic.getChildren() != null) {
            diagnostic.getChildren().forEach(d -> errors.addAll(extractValidationErrors(log, d, level + 1)));
        }

        return errors;
    }

    private static Diagnostic validate(EObject eObject) {
        BasicDiagnostic diagnostics = new BasicDiagnostic
                (EObjectValidator.DIAGNOSTIC_SOURCE,
                        0,
                        String.format("Diagnosis of %s\n", new Object[] { diagnostician.getObjectLabel(eObject) }),
                        new Object [] { eObject });
        diagnostician.validate(eObject, diagnostics, diagnostician.createDefaultContext());
        return diagnostics;
    }

}
