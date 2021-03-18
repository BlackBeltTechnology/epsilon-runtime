package hu.blackbelt.epsilon.runtime.execution.model.excel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.epsilon.common.module.ModuleElement;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.common.util.StringUtil;
import org.eclipse.epsilon.emc.spreadsheets.ISpreadsheetMetadata;
import org.eclipse.epsilon.emc.spreadsheets.ISpreadsheetMetadata.SpreadsheetWorksheetMetadata;
import org.eclipse.epsilon.emc.spreadsheets.MetadataXMLParser;
import org.eclipse.epsilon.emc.spreadsheets.SpreadsheetModel;
import org.eclipse.epsilon.emc.spreadsheets.SpreadsheetRow;
import org.eclipse.epsilon.emc.spreadsheets.SpreadsheetWorksheet;
import org.eclipse.epsilon.emc.spreadsheets.excel.ExcelRow;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.Collection;

public class ExcelModel extends SpreadsheetModel {
    /*
     * Public identifiers used for receiving parameters from Epsilon Development Tools
     */
    public static final String SPREADSHEET = "SPREADSHEET";
    public static final String CONFIGURATION = "CONFIGURATION";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelModel.class);
    public static final String SPREADSHEET_PASSWORD = "SPREADSHEET_PASSWORD";
    protected Workbook workbook;

    private URI spreadsheet;
    private URI configuration;
    private URIConverter uriConverter;

    private Document configurationDoc;
    private String password;

    public ExcelModel() {
        this((URIConverter) null);
    }
    
    public ExcelModel(URIHandler uriHandler) {
        super();
        this.uriConverter = new ExtensibleURIConverterImpl();
        this.uriConverter.getURIHandlers().add(0, uriHandler);
    }

    public ExcelModel(URIConverter uriConverter) {
        super();
        this.spreadsheet = null;
        this.configuration = null;
        this.configurationDoc = null;
        if (uriConverter == null) {
            this.uriConverter = new ExtensibleURIConverterImpl();
        }
    }

    /*
     * Password for test files out of box is "eps"
     */
    public static void main(final String[] args) throws Exception {
        ExcelModel model = new ExcelModel();
        if (args.length == 4) {
            model.setSpreadsheet(URI.createFileURI(args[0]));
            model.setConfiguration(URI.createFileURI(args[1]));
            model.setName(args[2]);
            model.setStoredOnDisposal(true);
        }
        model.load();

        System.out.println("*** Executing EOL Code...");
        EolModule module = new EolModule();
        module.parse("Sheet1.all.println();");
        // module.parse("Problems.all.println(); Problems.all.reqId.println(); Problems.all.reqId.name.println();"); //
        // module.parse("Requirement.all.println(); new Requirement(Map{'c_0'='12345'});"); //
        // module.parse("var s = new Sheet1(Map{'c_0'='12345'}); s.a.println();"); //
        // module.parse("var first = Sheet1.all.first(); first.println(); new Sheet2(Map{'c_0'='hi'});");
        module.getContext().getModelRepository().addModel(model);
        module.execute();

        model.store();
    }
    
    public void setSpreadsheet(final URI spreadsheet) {
        LOGGER.debug("Inside setSpreadsheet() method");
        LOGGER.debug("File path: '" + spreadsheet + "'");

        if (spreadsheet == null) {
            final String message = "Spreadsheet File must be provided";
            LOGGER.error(message);
            throw new IllegalArgumentException(message);
        }
        this.spreadsheet = spreadsheet;
    }

    public void setConfiguration(final URI configuration) throws ParserConfigurationException,
            SAXException, IOException {
        this.configuration = configuration;
        if (configuration != null) {
            this.configuration = configuration;
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            this.configurationDoc = documentBuilder.parse(uriConverter.createInputStream(configuration));
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void load(final StringProperties properties, final IRelativePathResolver resolver)
            throws EolModelLoadingException {
        super.load(properties, resolver);
        try {
            final String spreadsheet = properties.getProperty(ExcelModel.SPREADSHEET);
            this.setSpreadsheet(URI.createURI(spreadsheet));

            final String configuration = properties.getProperty(ExcelModel.CONFIGURATION);
            if (StringUtils.isNotEmpty(configuration)) {
                this.setConfiguration(URI.createURI(configuration));
            }

            this.setPassword(properties.getProperty(ExcelModel.SPREADSHEET_PASSWORD));

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new EolModelLoadingException(e, this);
        }
        this.load();
    }

    @Override
    protected void loadSpreadsheet() throws Exception {
        this.workbook = this.getWorkbook();
        for (int i = 0; i < this.workbook.getNumberOfSheets(); i++) {
            final Sheet sheet = this.workbook.getSheetAt(i);
            final ExcelWorksheet worksheet = new ExcelWorksheet(this, sheet, true);
            LOGGER.debug("Loaded worksheet from file: '" + worksheet.getName() + "'");
            this.addWorksheet(worksheet);
        }
    }

    private Workbook getWorkbook() throws IOException {
        if (spreadsheet.lastSegment().toLowerCase().endsWith("xlsx")) {
            return new XSSFWorkbook(this.getFileInputStream());
        } else {
            return new HSSFWorkbook(this.getFileInputStream());
        }
    }


    private InputStream getFileInputStream() throws IOException {
        if (!StringUtil.isEmpty(this.password)) {
            if (this.getIsXlsxFile()) {
                return this.getProtectedInputStreamForXlsx();
            } else {
                String message = "Cannot load password protected XLS files";
                LOGGER.error("Cannot load password protected XLS files");
                throw new UnsupportedOperationException("Cannot load password protected XLS files");
            }
        } else {
            return uriConverter.createInputStream(this.spreadsheet);
        }
    }

    private boolean getIsXlsxFile() {
        String name = spreadsheet.lastSegment();
        return name != null && name.toLowerCase().endsWith("xlsx");
    }

    private InputStream getProtectedInputStreamForXlsx() {
        try {
            POIFSFileSystem fileSystem = new POIFSFileSystem(uriConverter.createInputStream(this.spreadsheet));
            EncryptionInfo encryptionInfo = new EncryptionInfo(fileSystem);
            Decryptor decryptor = Decryptor.getInstance(encryptionInfo);
            decryptor.verifyPassword(this.password);
            return decryptor.getDataStream(fileSystem);
        } catch (Exception var4) {
            String message = "Failed to open file with the given password: " + var4.getMessage();
            LOGGER.error(message);
            throw new RuntimeException(message);
        }
    }

    @Override
    protected ISpreadsheetMetadata getSpreadsheetMetadata() {
        return new MetadataXMLParser(this.configurationDoc);
    }

    @Override
    protected boolean isMetadataConfigurationDefined() {
        return this.configuration != null && this.configurationDoc != null;
    }

    @Override
    protected ExcelWorksheet createWorksheet(final SpreadsheetWorksheetMetadata worksheetMetadata) {
        final Sheet sheet = this.workbook.createSheet(worksheetMetadata.getName());
        return new ExcelWorksheet(this, sheet, false);
    }

    @Override
    public boolean store(String location) {
        URI uri = URI.createURI(location);
        this.spreadsheet = uri;
        boolean result = store();
        return result;
    }

    @Override
    public boolean store() {
        try {
            this.deleteNonexistentWorksheets();
            this.writeFile();
            this.encryptFile();
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to write to workbook '" + this.name + "' " + e.getMessage());
            return false;
        }
    }

    /*
     * Apache POI will create an actual worksheet whenever a new Sheet object is
     * created from whatever has been defined in the configuration file when using
     * EOL. This method removes any worksheet that has been defined in configuration
     * file but has not actually been referenced in the program or defined to be
     * created on load.
     */
    private void deleteNonexistentWorksheets() {
        for (final SpreadsheetWorksheet worksheet : this.worksheets) {
            if (worksheet.getDoesNotExistInSpreadsheet()) {
                this.deleteWorksheet(worksheet);
            }
        }
    }

    private void writeFile() throws IOException {
        this.writeFile(null);
    }

    private void writeFile(final POIFSFileSystem fileSystem) throws IOException {

        try (OutputStream outputStream = uriConverter.createOutputStream(spreadsheet)) {
            if (fileSystem == null) {
                this.workbook.write(outputStream);
            }
            else {
                fileSystem.writeFilesystem(outputStream);
            }
        }
    }

    private void encryptFile() throws Exception {
        if (!StringUtil.isEmpty(this.password) && this.getIsXlsxFile()) {
            final POIFSFileSystem fs = new POIFSFileSystem(uriConverter.createInputStream(this.spreadsheet));
            final EncryptionInfo info = new EncryptionInfo(fs);

            final Encryptor enc = info.getEncryptor();
            enc.confirmPassword(this.password);

            try (OPCPackage opc = OPCPackage.open(uriConverter.createInputStream(this.spreadsheet))) {
                OutputStream os = enc.getDataStream(fs);
                opc.save(os);
            }

            this.writeFile(fs);
        }
    }

    @Override
    public void deleteWorksheet(final SpreadsheetWorksheet worksheet) {
        final int worksheetIndex = this.workbook.getSheetIndex(((ExcelWorksheet) worksheet).sheet);
        this.workbook.removeSheetAt(worksheetIndex);
    }

    @Override
    public Collection<SpreadsheetRow> find(Variable iterator, ModuleElement ast, IEolContext context)
            throws EolRuntimeException {
        throw new UnsupportedOperationException();
    }
    /*
    @Override
    protected ISpreadsheetMetadata getSpreadsheetMetadata() {
        return new MetadataXMLParser(this.configurationDoc);
    }

    @Override
    protected boolean isMetadataConfigurationDefined() {
        return this.configuration != null && this.configurationDoc != null;
    }

    @Override
    protected ExcelWorksheet createWorksheet(final SpreadsheetWorksheetMetadata worksheetMetadata) {
        final Sheet sheet = this.workbook.createSheet(worksheetMetadata.getName());
        return new ExcelWorksheet(this, sheet, false);
    }

    @Override
    public boolean store(String location) {
        URI uri = URI.createURI(location);
        this.spreadsheet = uri;
        boolean result = store();
        return result;
    }

    @Override
    public boolean store() {
        try {
            this.deleteNonexistentWorksheets();
            this.writeFile();
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to write to workbook '" + this.name + "' " + e.getMessage());
            return false;
        }
    }

    / *
     * Apache POI will create an actual worksheet whenever a new Sheet object is created from whatever has been defined
     * in the configuration file when using EOL. This method removes any worksheet that has been defined in
     * configuration file but has not actually been referenced in the program or defined to be created on load.
     * /
    private void deleteNonexistentWorksheets() {
        for (final SpreadsheetWorksheet worksheet : this.worksheets) {
            if (worksheet.getDoesNotExistInSpreadsheet()) {
                this.deleteWorksheet((ExcelWorksheet) worksheet);
            }
        }
    }

    private void writeFile() throws IOException {
        this.writeFile(null);
    }

    private void writeFile(final POIFSFileSystem fileSystem) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = uriConverter.createOutputStream(spreadsheet);
            if (fileSystem == null) {
                this.workbook.write(outputStream);
            } else {
                fileSystem.writeFilesystem(outputStream);
            }
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }

    }

    @Override
    public void deleteWorksheet(final SpreadsheetWorksheet worksheet) {
        final int worksheetIndex = this.workbook.getSheetIndex(((ExcelWorksheet) worksheet).sheet);
        this.workbook.removeSheetAt(worksheetIndex);

    }

    @Override
    public Collection<SpreadsheetRow> find(Variable iterator, ModuleElement ast, IEolContext context) throws EolRuntimeException {
        throw new UnsupportedOperationException();
    } */
}
