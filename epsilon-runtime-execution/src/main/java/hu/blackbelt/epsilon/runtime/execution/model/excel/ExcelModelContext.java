package hu.blackbelt.epsilon.runtime.execution.model.excel;

/*-
 * #%L
 * epsilon-runtime-execution
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import hu.blackbelt.epsilon.runtime.execution.api.ModelContext;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.IModel;
import org.eclipse.epsilon.eol.models.ModelReference;
import org.eclipse.epsilon.eol.models.ModelRepository;

import java.util.List;
import java.util.Map;

@Data
@Builder(builderMethodName = "excelModelContextBuilder")
@EqualsAndHashCode
public class ExcelModelContext implements ModelContext {


    public static final String EXCEL_CONFIGURATION = "excelConfiguration";
    public static final String EXCEL = "excel";
    String spreadSheetPassword;


    @NonNull
    String name;

    @Builder.Default
    List<String> aliases = ImmutableList.of();

    @Builder.Default
    boolean readOnLoad = true;

    @Builder.Default
    boolean storeOnDisposal = false;

    @Builder.Default
    boolean cached = true;

    @NonNull
    String excel;

    String excelConfiguration;

    @Override
    public String toString() {
        return "ExcelModelContext{" +
                "artifacts='" + getArtifacts() + '\'' +
                ", name='" + name + '\'' +
                ", aliases=" + aliases +
                ", uriConverterMap='" + getUriConverterMap() + '\'' +
                ", readOnLoad=" + readOnLoad +
                ", storeOnDisposal=" + storeOnDisposal +
                ", cached=" + cached +
                ", spreadSheetPassword='" + spreadSheetPassword + '\'' +
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
    public Map<String, String> getArtifacts() {
        return ImmutableMap.of(EXCEL, excel, EXCEL_CONFIGURATION,  excelConfiguration);
    }

    @Override
    public Map<String, String> getUriConverterMap() {
        return ImmutableMap.of();
    }

    @Override
    public IModel load(Logger log, ResourceSet resourceSet, ModelRepository repository, Map<String, URI> uri, Map<URI, URI> uriMap) throws EolModelLoadingException {
        return ExcelModelUtil.loadExcel(log, repository, this, uri.get(EXCEL), uri.get(EXCEL_CONFIGURATION));
    }

}
