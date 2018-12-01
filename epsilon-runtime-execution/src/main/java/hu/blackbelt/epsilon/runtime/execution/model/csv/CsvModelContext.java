package hu.blackbelt.epsilon.runtime.execution.model.csv;

import hu.blackbelt.epsilon.runtime.execution.Log;
import hu.blackbelt.epsilon.runtime.execution.ModelContext;
import lombok.Builder;
import lombok.Data;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelReference;
import org.eclipse.epsilon.eol.models.ModelRepository;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class CsvModelContext implements ModelContext {

    Map<String, String> artifacts;

    String name;

    List<String> aliases;

    @Builder.Default
    boolean readOnLoad = true;

    @Builder.Default
    boolean storeOnDisposal = false;

    @Builder.Default
    boolean cached = true;

    /** The field separator. */
    @Builder.Default
    protected String fieldSeparator = ",";

    /** The has known headers. */
    @Builder.Default
    protected boolean knownHeaders = false;

    /** The has varargs headers. */
    @Builder.Default
    protected boolean varargsHeaders = false;

    @Override
    public String toString() {
        return "CsvModelContext{" +
                "artifacts='" + artifacts + '\'' +
                ", name='" + name + '\'' +
                ", aliases=" + aliases +
                ", readOnLoad=" + readOnLoad +
                ", storeOnDisposal=" + storeOnDisposal +
                ", cached=" + cached +
                ", fieldSeparator='" + fieldSeparator +
                ", knownHeaders='" + knownHeaders +
                ", varargsHeaders='" + varargsHeaders + '\'' +
                '}';
    }

    @Override
    public void addAliases(ModelRepository repository, ModelReference ref) {
        ref.setName(this.getName());
        if (this.getAliases() != null) {
            for (String alias : this.getAliases()) {
                ref.getAliases().add(alias);
            }
        }
        repository.addModel(ref);
    }

    @Override
    public IModel load(Log log, ResourceSet resourceSet, ModelRepository repository, Map<String, URI> uri) throws EolModelLoadingException {
        return CsvModelUtil.loadCsv(log, repository, this, uri.get("csv"));
    }

}
