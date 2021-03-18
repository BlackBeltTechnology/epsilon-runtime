package hu.blackbelt.epsilon.runtime.execution.model.excel;

import com.google.common.base.Strings;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.ModelRepository;

import static java.util.stream.Collectors.joining;

public class ExcelModelUtil {


    public static ExcelModel loadExcel(Log log, ModelRepository repository, ExcelModelContext excelModelContext, URI excel, URI excelConfiguration) throws EolModelLoadingException {

        org.eclipse.epsilon.emc.spreadsheets.excel.ExcelModel ddd;
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
