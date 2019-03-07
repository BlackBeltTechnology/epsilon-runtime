package hu.blackbelt.epsilon.runtime.execution.model.emf;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import lombok.*;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.epsilon.emc.emf.EmfModel;

import java.util.HashSet;


@AllArgsConstructor
@Builder
public class DefaultRuntimeEmfModelFactory implements EmfModelFactory {

    @NonNull
    Log log;

    @Override
    public EmfModel create(ResourceSet resourceSet) {
        return new EmfModel() {
            @Override
            @SneakyThrows
            protected ResourceSet createResourceSet() {
                ResourceSet emfReourceSet =  super.createResourceSet();

                for (URIHandler uriHandler : resourceSet.getURIConverter().getURIHandlers()) {
                    int idx = resourceSet.getURIConverter().getURIHandlers().indexOf(uriHandler);
                    if (!emfReourceSet.getURIConverter().getURIHandlers().contains(uriHandler)) {
                        log.info("    Adding uri handler: " + uriHandler.getClass().getName());
                        emfReourceSet.getURIConverter().getURIHandlers().add(idx, uriHandler);
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
