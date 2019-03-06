package hu.blackbelt.epsilon.runtime.execution.model.emf;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.emc.emf.EmfModel;

public class DefaultRuntimeEmfModelFactory implements EmfModelFactory {
    @Override
    public EmfModel create(ResourceSet resourceSet) {
        return new DefaultRuntimeEmfModel(resourceSet);
    }
}
