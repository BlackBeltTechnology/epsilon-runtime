# osgi Specification

## Purpose

The osgi module provides an EMF `URIHandler` implementation that resolves model resources from OSGi bundles, enabling epsilon-runtime to load metamodels and models from deployed OSGi bundles in Apache Karaf containers.

## Architecture

Single class `BundleURIHandler` in `hu.blackbelt.epsilon.runtime.osgi`:
- Extends EMF's `URIHandlerImpl`
- Read-only handler (write operations throw `IOException`)
- Resolves URIs with a custom scheme to entries inside an OSGi `Bundle`
- Configured with a URI scheme prefix, a root path within the bundle, and the `Bundle` reference

## Requirements

### Requirement: Read resources from OSGi bundles

`BundleURIHandler` SHALL resolve URIs with the configured scheme to entries within an OSGi bundle, returning an `InputStream` for reading.

#### Scenario: Load model from bundle
- **GIVEN** a `BundleURIHandler` configured with scheme `"bundleresource"`, root path `"/models"`, and a valid OSGi `Bundle`
- **WHEN** `createInputStream(URI.createURI("bundleresource:my-model.xmi"), options)` is called
- **THEN** an `InputStream` is returned reading from the bundle entry at `/models/my-model.xmi`

### Requirement: URI scheme matching

`BundleURIHandler.canHandle(uri)` SHALL return `true` only for URIs whose scheme matches the configured scheme prefix.

#### Scenario: Matching scheme
- **GIVEN** a `BundleURIHandler` configured with scheme `"bundleresource"`
- **WHEN** `canHandle(URI.createURI("bundleresource:model.ecore"))` is called
- **THEN** it returns `true`

#### Scenario: Non-matching scheme
- **GIVEN** a `BundleURIHandler` configured with scheme `"bundleresource"`
- **WHEN** `canHandle(URI.createURI("file:/model.ecore"))` is called
- **THEN** it returns `false`

### Requirement: Read-only enforcement

`BundleURIHandler` SHALL throw `IOException` for write operations (`createOutputStream`), as bundle resources are read-only.

#### Scenario: Reject write operation
- **GIVEN** a `BundleURIHandler`
- **WHEN** `createOutputStream(uri, options)` is called
- **THEN** an `IOException` is thrown indicating that bundle resources are read-only

### Requirement: Bundle entry existence check

`BundleURIHandler.exists(uri, options)` SHALL check whether the corresponding entry exists in the OSGi bundle.

#### Scenario: Entry exists
- **GIVEN** a bundle containing the entry `/models/metamodel.ecore`
- **WHEN** `exists(URI.createURI("bundleresource:metamodel.ecore"), options)` is called
- **THEN** it returns `true`

#### Scenario: Entry missing
- **GIVEN** a bundle that does NOT contain `/models/missing.ecore`
- **WHEN** `exists(URI.createURI("bundleresource:missing.ecore"), options)` is called
- **THEN** it returns `false`
