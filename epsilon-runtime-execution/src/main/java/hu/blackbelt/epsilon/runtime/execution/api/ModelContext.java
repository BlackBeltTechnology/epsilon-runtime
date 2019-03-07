package hu.blackbelt.epsilon.runtime.execution.api;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelReference;
import org.eclipse.epsilon.eol.models.ModelRepository;

import java.util.List;
import java.util.Map;

public interface ModelContext {

    IModel load(Log log, ResourceSet resourceSet, ModelRepository repository, Map<String, URI> uris) throws EolModelLoadingException;
    void addAliases(ModelRepository repository, ModelReference ref);
    List<String> getAliases();

    Map<String, String> getArtifacts();

}
