# execution Specification

## Purpose

The execution module is the core of epsilon-runtime. It provides `ExecutionContext`, a fluent builder-based orchestrator that loads EMF metamodels and models (EMF, XML, Excel, PlainXML), executes Epsilon scripts (EOL, ETL, EVL, EGL, EGX, ECL, EML, HUTN), and manages the model lifecycle including commit, rollback, and resource cleanup.

## Architecture

The central class is `ExecutionContext` (implements `AutoCloseable`), which holds a `ResourceSet`, a list of `ModelContext` implementations, and a `ModelRepository`. Each Epsilon language has a corresponding execution context class that extends `EolExecutionContext`. Model loading is delegated to `ModelContext` implementations, each backed by a factory (`EmfModelFactory`, `XmlModelFactory`). URI resolution is handled by pluggable `URIHandler` implementations.

Key classes:
- `ExecutionContext` — orchestrator (builder, load, execute, commit, close)
- `ModelContext` — interface for model loading (implementations: `EmfModelContext`, `WrappedEmfModelContext`, `XmlModelContext`, `ExcelModelContext`, `PlainXmlModelContext`)
- `EolExecutionContext` — base for script execution (subclasses: `EtlExecutionContext`, `EvlExecutionContext`, `EglExecutionContext`, `EgxExecutionContext`, `EclExecutionContext`, `EmlExecutionContext`)
- `HutnExecutionContext` — separate context for HUTN-to-EMF transformation
- URI handlers: `NioFilesystemnRelativePathURIHandlerImpl`, `NameMappedURIHandlerImpl`, `CompositeURIHandlerImpl`, `IgnoreOwnBaseUriURIHandler`
- Logging: `BufferedSlf4jLogger`, `StringBuilderLogger`, `LoggingOutputStream`
- Validation: `ModelValidator`, `EcoreUriFixer`

## Requirements

### Requirement: ExecutionContext lifecycle management

The `ExecutionContext` SHALL manage the complete lifecycle of metamodel registration, model loading, script execution, and resource cleanup as an `AutoCloseable` resource.

#### Scenario: Successful transformation execution
- **GIVEN** an `ExecutionContext` built with metamodel URIs, model contexts, and a resource set
- **WHEN** `load()` is called followed by `executeProgram(etlExecutionContext)` and then `commit()`
- **THEN** metamodels are registered as EPackages, models are loaded into the model repository, the ETL script is parsed and executed, and model changes are persisted

#### Scenario: Rollback on close
- **GIVEN** an `ExecutionContext` built with `rollback(true)` and models with `storeOnDisposal(true)`
- **WHEN** `close()` is called without calling `commit()`
- **THEN** all model changes are rolled back and resources are disposed without persisting

### Requirement: EMF model loading

`EmfModelContext` SHALL load EMF models from URIs using a configurable `EmfModelFactory`, supporting caching, parallel access, validation, and custom URI converters.

#### Scenario: Load EMF model with validation
- **GIVEN** an `EmfModelContext` with `validateModel(true)` and an EMF model URI
- **WHEN** the model is loaded via `ExecutionContext.load()`
- **THEN** the model is loaded, validated against its Ecore metamodel, and a `ModelValidationException` is thrown if validation fails

#### Scenario: Load wrapped EMF resource
- **GIVEN** a `WrappedEmfModelContext` with an existing in-memory EMF `Resource`
- **WHEN** the model is loaded
- **THEN** the existing resource is wrapped in an `InMemoryEmfModel` without re-reading from disk

### Requirement: XML model loading with XSD

`XmlModelContext` SHALL load XML documents as EMF models using an XSD schema as the metamodel, supporting read-on-load and store-on-disposal semantics.

#### Scenario: Transform EMF to XML via ETL
- **GIVEN** an EMF source model and an `XmlModelContext` with `readOnLoad(false)` and `storeOnDisposal(true)`
- **WHEN** an ETL transformation populates the XML model and `commit()` is called
- **THEN** the XML output file is written conforming to the XSD schema

### Requirement: Excel model loading

`ExcelModelContext` SHALL load Excel spreadsheets as Epsilon models using an XML configuration file that maps sheet columns to model properties.

#### Scenario: Load Excel model
- **GIVEN** an `ExcelModelContext` with an Excel file path and configuration XML
- **WHEN** the model is loaded
- **THEN** spreadsheet data is accessible as model elements within Epsilon scripts

### Requirement: Multiple Epsilon language support

The module SHALL support execution of all major Epsilon language dialects via dedicated execution context classes.

#### Scenario: Execute ETL transformation with trace export
- **GIVEN** an `EtlExecutionContext` with `exportTransformationTrace("traceKey")`
- **WHEN** the ETL script executes
- **THEN** the transformation trace is stored in the execution context map under the specified key

#### Scenario: Execute EVL validation with expected errors
- **GIVEN** an `EvlExecutionContext` with `expectedErrors(["constraint1"])` and `expectedWarnings(["warning1"])`
- **WHEN** the EVL script executes
- **THEN** an `EvlScriptExecutionException` is thrown if actual errors/warnings do not match expected ones

#### Scenario: Execute EGL code generation
- **GIVEN** an `EglExecutionContext` with an `outputRoot` directory
- **WHEN** the EGL template executes
- **THEN** generated files are written to the output root directory

### Requirement: Custom URI resolution

The module SHALL support pluggable URI handlers for resolving model resources from different sources (filesystem, mapped names, composites).

#### Scenario: Resolve URIs via NIO filesystem handler
- **GIVEN** a `NioFilesystemnRelativePathURIHandlerImpl` configured with scheme `"urn"` and a root path
- **WHEN** a URI like `urn:model.xmi` is resolved
- **THEN** it maps to `{rootPath}/model.xmi` on the filesystem

#### Scenario: Resolve URIs via name mapping
- **GIVEN** a `NameMappedURIHandlerImpl` with a mapping from `urn:logical` to `file:/physical`
- **WHEN** the URI `urn:logical` is resolved
- **THEN** it delegates to the appropriate handler for `file:/physical`

### Requirement: Model validation

`ModelValidator` SHALL validate EMF models against their Ecore metamodel constraints using the EMF Diagnostician.

#### Scenario: Detect invalid model
- **GIVEN** an EMF model that violates a required attribute constraint in its metamodel
- **WHEN** `ModelValidator.validate(model)` is called
- **THEN** a `ModelValidationException` is thrown containing the list of validation errors
