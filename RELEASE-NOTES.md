## epsilon-runtime 1.2.0

This release contains major changes which is compromise the compatibility with the previous version. When you update your code also have to be updated.

### Compatibility breaks

* The builder names contains the bulded class name instead of the .build() method.
* UML namespace isn't registered by default, he addUmlPackage variable have to be used on ExecutionContext
* ArtifactResolver removed, the new URIHandler mechanism replaced it.
* Module ExecutionContexts default constructors removed

#### Model contexts
* Artifact's map are removed, insted of fields are used.
* platformAlias renamed to referenceUri
* metamodelFile, metaModelUris, fileBasedMetamodelUris, reuseUnmodifiedFileBasedMetamodels removed, the meta namespaces have to be registered in metamodels on ExecutionContext
* EmfodelFactory can be replaced.
* excelSheet renamed to excel

* Tests added
PLUGIN: models renamed to emfModels
Uri converters can be added by model. (to be able to handle model references)

### ChangeLog



## epsilon-runtime 1.1.5

### ChangeLog

#### Bug

#### New feature

* [#6] Support of export trace data in ETL transformations
* [#9] Update epsilon to 1.5.1

## epsilon-runtime 1.1.4

### ChangeLog

#### Bug

* [#2] Append sourceDirectory prefix only if path of Epsilon script is relative

#### New feature

* [#1] Verify EVL rules
* [#3] Model syntax validation
