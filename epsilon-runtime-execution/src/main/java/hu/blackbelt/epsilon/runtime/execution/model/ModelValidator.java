package hu.blackbelt.epsilon.runtime.execution.model;

import com.google.common.base.Strings;
import hu.blackbelt.epsilon.runtime.execution.Log;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.epsilon.emc.emf.EmfModel;

import java.util.LinkedList;
import java.util.List;

public class ModelValidator {

    public static List<String> getValidationErrors(Log log, EmfModel model) {
        final List<String> errors = new LinkedList<>();

        model.allContents().forEach(e -> errors.addAll(extractValidationErrors(log, Diagnostician.INSTANCE.validate(e), 0)));

        return errors;
    }

    private static List<String> extractValidationErrors(Log log, Diagnostic diagnostic, int level) {
        final List<String> errors = new LinkedList<>();

        final String message = diagnostic.getMessage();
        final String indent = Strings.padStart("", level * 2, ' ');
        switch (diagnostic.getSeverity()) {
            case Diagnostic.INFO: {
                log.info(indent + message);
                break;
            }
            case Diagnostic.WARNING: {
                log.warn(indent + message);
                break;
            }
            case Diagnostic.ERROR: {
                log.error(indent + message);
                errors.add(message);
                break;
            }
        }

        if (diagnostic.getChildren() != null) {
            diagnostic.getChildren().forEach(d -> errors.addAll(extractValidationErrors(log, d, level + 1)));
        }

        return errors;
    }
}
