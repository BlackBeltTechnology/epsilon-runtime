package hu.blackbelt.epsilon.runtime.execution.model.csv;

import com.google.common.base.Strings;
import hu.blackbelt.epsilon.runtime.execution.Log;
import org.eclipse.emf.common.util.URI;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.csv.CsvModel;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.emc.spreadsheets.excel.ExcelModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.models.ModelRepository;

import static java.util.stream.Collectors.joining;

public class CsvModelUtil {


    public static CsvModel loadCsv(Log log, ModelRepository repository, CsvModelContext csvModelContext, URI csvFile) throws EolModelLoadingException {

        final CsvModel model = new CsvModel();

        final StringProperties properties = new StringProperties();
        properties.put(CsvModel.PROPERTY_NAME, csvModelContext.getName() + "");
        if (csvModelContext.getAliases() != null) {
            properties.put(CsvModel.PROPERTY_ALIASES, csvModelContext.getAliases().stream().collect(joining(",")) + "");
        } else {
            properties.put(CsvModel.PROPERTY_ALIASES, "");
        }
        properties.put(CsvModel.PROPERTY_READONLOAD, csvModelContext.isReadOnLoad()+ "");
        properties.put(CsvModel.PROPERTY_STOREONDISPOSAL, csvModelContext.isStoreOnDisposal() + "");
        if (!Strings.isNullOrEmpty(csvModelContext.getFieldSeparator())) {
            properties.put(CsvModel.PROPERTY_FIELD_SEPARATOR, csvModelContext.getFieldSeparator() + "");
        }
        properties.put(CsvModel.PROPERTY_HAS_KNOWN_HEADERS, csvModelContext.isKnownHeaders() + "");
        properties.put(CsvModel.PROPERTY_HAS_VARARGS_HEADERS, csvModelContext.isVarargsHeaders() + "");

        properties.put(CsvModel.PROPERTY_FILE, csvFile);

        model.load(properties);
        model.setName(csvModelContext.getName());
        repository.addModel(model);
        return model;
    }


}
