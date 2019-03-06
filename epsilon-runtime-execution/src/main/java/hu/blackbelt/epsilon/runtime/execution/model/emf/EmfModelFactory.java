package hu.blackbelt.epsilon.runtime.execution.model.emf;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.emc.emf.EmfModel;

public interface EmfModelFactory {

    EmfModel create(ResourceSet resourceSet);
}
