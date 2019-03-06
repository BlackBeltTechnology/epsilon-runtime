package hu.blackbelt.epsilon.runtime.execution.model.xml;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.emc.emf.xml.XmlModel;

public class DefaultRuntimeXmlModelFactory implements XmlModelFactory {
    @Override
    public XmlModel create(ResourceSet resourceSet) {
        return new DefaultRuntimeXmlModel(resourceSet);
    }
}
