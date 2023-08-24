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

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.ModelRepository;

import static java.util.stream.Collectors.joining;

public class ExcelModelUtil {


    public static ExcelModel loadExcel(Logger log, ModelRepository repository, ExcelModelContext excelModelContext, URI excel, URI excelConfiguration) throws EolModelLoadingException {

        final ExcelModel model = new ExcelModel();

        final StringProperties properties = new StringProperties();
        properties.put(ExcelModel.PROPERTY_NAME, excelModelContext.getName() + "");
        if (excelModelContext.getAliases() != null) {
            properties.put(ExcelModel.PROPERTY_ALIASES, excelModelContext.getAliases().stream().collect(joining(",")) + "");
        } else {
            properties.put(ExcelModel.PROPERTY_ALIASES, "");
        }
        properties.put(ExcelModel.PROPERTY_READONLOAD, excelModelContext.isReadOnLoad()+ "");
        properties.put(ExcelModel.PROPERTY_STOREONDISPOSAL, excelModelContext.isStoreOnDisposal() + "");
        if (excelConfiguration != null) {
            properties.put(ExcelModel.CONFIGURATION, excelConfiguration);
        }

        properties.put(ExcelModel.SPREADSHEET, excel);

        model.load(properties);
        model.setName(excelModelContext.getName());
        repository.addModel(model);
        return model;
    }


}
