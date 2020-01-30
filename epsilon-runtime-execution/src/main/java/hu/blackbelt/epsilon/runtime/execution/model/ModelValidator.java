package hu.blackbelt.epsilon.runtime.execution.model;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
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

    public static void validate(Optional<Log> log, EmfModel model) throws ModelValidationException {

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

    public static List<String> getValidationErrors(Optional<Log> log, EmfModel model) {
        final List<String> errors = new LinkedList<>();

        model.allContents().forEach(e -> errors.addAll(extractValidationErrors(log, validate(e), 0)));

        return errors;
    }

    private static List<String> extractValidationErrors(Optional<Log> log, Diagnostic diagnostic, int level) {
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
