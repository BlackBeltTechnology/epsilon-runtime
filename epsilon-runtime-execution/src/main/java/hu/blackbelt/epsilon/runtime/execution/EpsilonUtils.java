package hu.blackbelt.epsilon.runtime.execution;

import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.IReflectiveModel;
import org.eclipse.epsilon.eol.models.ModelReference;
import org.eclipse.epsilon.eol.models.ReflectiveModelReference;

public class EpsilonUtils {
    public static ModelReference createModelReference(IModel model) {
        if (model instanceof IReflectiveModel) {
            return new ReflectiveModelReference((IReflectiveModel)model);

        } else {
            return new ModelReference(model);
        }
    }
}
