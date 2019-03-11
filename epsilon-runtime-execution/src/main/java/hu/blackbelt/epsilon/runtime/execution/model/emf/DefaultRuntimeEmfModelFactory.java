package hu.blackbelt.epsilon.runtime.execution.model.emf;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import lombok.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.epsilon.emc.emf.EmfModel;

import java.util.HashSet;
import java.util.Map;


@AllArgsConstructor
@Builder
public class DefaultRuntimeEmfModelFactory implements EmfModelFactory {

    @NonNull
    Log log;

    @Override
    public EmfModel create(ResourceSet resourceSet, Map<URI, URI> uriMapConverter) {
        return new EmfModel() {
            @Override
            @SneakyThrows
            protected ResourceSet createResourceSet() {
                ResourceSet emfReourceSet =  super.createResourceSet();

                for (URIHandler uriHandler : resourceSet.getURIConverter().getURIHandlers()) {
                    int idx = resourceSet.getURIConverter().getURIHandlers().indexOf(uriHandler);
                    if (!emfReourceSet.getURIConverter().getURIHandlers().contains(uriHandler)) {
                        log.info("    Adding uri handler: " + uriHandler.toString());
                        emfReourceSet.getURIConverter().getURIHandlers().add(idx, uriHandler);
                    }
                }

                for (URI key : resourceSet.getURIConverter().getURIMap().keySet()) {
                    if (!emfReourceSet.getURIConverter().getURIMap().containsKey(key)) {
                        URI value = resourceSet.getURIConverter().getURIMap().get(key);
                        log.info("    Adding reference URI converter: " + key + " -> " + value);
                        emfReourceSet.getURIConverter().getURIMap().put(key, value);
                    }
                }

                if (uriMapConverter != null) {
                    for (URI from : uriMapConverter.keySet()) {
                        URI to = uriMapConverter.get(from);
                        log.info(String.format("    Registering URI converter: %s -> %s", from.toString(), to.toString()));
                        emfReourceSet.getURIConverter().getURIMap().put(from, to);
                    }
                }


                for (String key : new HashSet<String>(resourceSet.getPackageRegistry().keySet())) {
                    EPackage ePackage = resourceSet.getPackageRegistry().getEPackage(key);
                    emfReourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
                }
                return emfReourceSet;
            }
        };
    }}
