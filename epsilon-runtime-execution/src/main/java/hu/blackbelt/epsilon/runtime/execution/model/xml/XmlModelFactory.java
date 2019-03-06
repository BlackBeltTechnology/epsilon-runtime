package hu.blackbelt.epsilon.runtime.execution.model.xml;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.emc.emf.xml.XmlModel;

public interface XmlModelFactory {

    XmlModel create(ResourceSet resourceSet);
}
