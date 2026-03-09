# epsilon-runtime - Project Documentation

## Project Overview


**Repository:** BlackBeltTechnology/epsilon-runtime
**License:** Apache License 2.0
**Java Version:** 21 (Zulu JDK)
**Build System:** Maven 3.9.4+ with Maven wrapper (`./mvnw`), CI-friendly versioning via `${revision}`

1. Wraps the [Eclipse Epsilon](https://www.eclipse.org/epsilon/) model transformation, validation, and code generation framework for use outside the Eclipse IDE
2. Provides a fluent builder API (`ExecutionContext`) for loading EMF metamodels/models, executing Epsilon scripts (EOL, ETL, EVL, EGL, EGX, ECL, EML, HUTN), and managing model lifecycle
3. Supports multiple model types: EMF/Ecore (`.ecore`/`.xmi`), XML with XSD, Excel spreadsheets, and plain XML
4. Dual deployment: standalone Maven projects and Apache Karaf/OSGi containers via bundle packaging and Karaf feature/KAR modules
5. Includes custom URI handlers for resolving model resources from filesystems, OSGi bundles, and mapped URIs

## Code Instructions

1. First think through the problem, read the codebase for relevant files.
2. Before you make any major changes, check in with me and I will verify the plan.
3. Please every step of the way just give me a high level explanation of what changes you made.
4. Make every task and code change you do as simple as possible. We want to avoid making any massive or complex changes. Every change should impact as little code as possible. Everything is about simplicity.
5. Maintain a documentation file that describes how the architecture of the app works inside and out.
6. Never speculate about code you have not opened. If the user references a specific file, you MUST read the file before answering. Make sure to investigate and read relevant files BEFORE answering questions about the codebase. Never make any claims about code before investigating unless you are certain of the correct answer - give grounded and hallucination-free answers.
7. For implementation use TDD (Test-Driven Development): write or update tests first to define the expected behaviour, verify they fail, then write the minimal implementation to make them pass.
8. Use DRY (Don't Repeat Yourself): extract reusable logic into separate classes, utilities, or components. If the same pattern appears in multiple places, refactor it into a shared helper.

## Directory Structure

```
epsilon-runtime/
├── epsilon-runtime-execution/   # Core execution engine (main module)
├── epsilon-runtime-utils/       # Small utilities (UUID, MD5, abbreviation)
├── epsilon-runtime-eol-patch/   # Patches for Epsilon EOL internals
├── epsilon-runtime-osgi/        # OSGi BundleURIHandler
├── features/                    # Karaf feature descriptor
├── kar/                         # Karaf KAR archive assembly
├── epsilon-runtime-reports/     # JaCoCo aggregate reports
├── .github/workflows/           # GitHub Actions CI/CD pipelines
├── .mvn/                        # Maven wrapper and JVM config
└── pom.xml                      # Parent POM with all dependency management
```

## Core Modules

### Execution & Business Logic

| Module | Type | Purpose |
|--------|------|---------|
| `epsilon-runtime-execution/` | OSGi bundle | Core module: `ExecutionContext` orchestrator, `ModelContext` abstraction, language-specific execution contexts (EOL/ETL/EVL/EGL/EGX/ECL/EML/HUTN), URI handlers, model loading/validation, EMF/XML/Excel model support |
| `epsilon-runtime-utils/` | OSGi bundle | Utility classes: `UUIDUtils` (v3 UUID generation), `MD5Utils` (MD5 hashing), `AbbreviateUtils` (string abbreviation by vowel removal) |
| `epsilon-runtime-eol-patch/` | OSGi bundle | Patches to Epsilon internals: `EolModelElementTypeNotFoundException` (suppresses stack traces for performance), `ReflectionUtil` (enhanced method discovery and invocation) |

### OSGi Deployment

| Module | Type | Purpose |
|--------|------|---------|
| `epsilon-runtime-osgi/` | OSGi bundle | `BundleURIHandler` — read-only EMF URIHandler that resolves resources from OSGi bundles using custom URI schemes |
| `features/` | Karaf feature | Defines OSGi feature descriptor for deployment in Apache Karaf containers |
| `kar/` | Karaf KAR | Packages all bundles and features into a deployable Karaf archive (`.kar`) |

### Reporting

| Module | Type | Purpose |
|--------|------|---------|
| `epsilon-runtime-reports/` | POM | Aggregates JaCoCo code coverage reports across all modules |

## Technology Stack

### Core Technologies
- **Eclipse Epsilon 2.8.0** — Model transformation/validation/generation language family
- **Eclipse EMF 2.17.0** — Eclipse Modeling Framework for metamodel-based model handling
- **Eclipse XSD 2.17.0** — XML Schema Definition support for XML model loading
- **Apache Karaf 4.4.7** — OSGi container for runtime deployment
- **Lombok 1.18.34** — Annotation-based code generation (`@Builder`, `@Getter`, `@Setter`, etc.)
- **Guava 30.0-jre** — Google core libraries (ImmutableList, etc.)
- **Apache POI** — Excel spreadsheet reading (via Epsilon spreadsheet EMC)

### Build & Quality
- **Maven 3.9.4+** with Maven wrapper (`./mvnw`)
- **JUnit Jupiter 5.9.0** — Unit testing
- **Maven Surefire 3.5.1** — Test execution
- **JaCoCo 0.8.12** — Code coverage
- **SonarQube** — Code quality analysis (sonar.judo.technology, runs on develop branch)
- **Apache Felix maven-bundle-plugin 5.1.8** — OSGi bundle packaging
- **flatten-maven-plugin 1.3.0** — CI-friendly `${revision}` version resolution
- **lombok-maven-plugin** — Delombok for Javadoc generation

## Build Commands

```bash
# Full build (with tests)
./mvnw clean install

# Build without tests
./mvnw clean install -DskipTests

# Run all tests
./mvnw clean test

# Run a single test class
./mvnw -pl epsilon-runtime-execution test -Dtest=ExecutionContextTest

# Run a single test method
./mvnw -pl epsilon-runtime-execution test -Dtest=ExecutionContextTest#testExecuteEtlWithContextVariables

# Build specific module
./mvnw -pl epsilon-runtime-execution clean install

# Skip all non-core modules
./mvnw clean install -DskipModules=true
```

### Maven Profiles

| Profile | Purpose |
|---------|---------|
| `modules` | Default — builds all 7 modules (active unless `-DskipModules=true`) |
| `sign-artifacts` | Signs build artifacts with GPG for release |
| `release-judong` | Deploys to Judong Nexus (internal: nexus.judo.technology) |
| `release-central` | Deploys to Maven Central via Sonatype OSSRH |
| `release-dummy` | Deploys to local filesystem (`/tmp/`) for testing |
| `generate-github-asciidoc-diagrams` | Generates documentation diagrams from AsciiDoc sources |
| `update-source-code-license` | Updates Apache 2.0 license headers in all source files |

## Key Configuration Files

| File | Purpose |
|------|---------|
| `pom.xml` | Parent POM: all dependency versions, plugin management, profiles, module list |
| `.mvn/jvm.config` | JVM args for Maven: `-Xms1024m -Xmx2048m` plus Java module `--add-opens` for reflection |
| `.mvn/extensions.xml` | Maven wagon extensions for file and WebDAV protocols |
| `logback-test.xml` | Logback configuration for test runs (referenced via `logback-test-config` property) |
| `features/src/main/feature/feature.xml` | Karaf feature descriptor listing all OSGi bundles |

## Development Environment

**Required:**
- Java 21 JDK (Zulu recommended)
- Maven 3.9.4+ (or use `./mvnw`)

**Recommended IDE setup:**
- Disable Java auto-formatting (project does not enforce a formatter)
- Disable organize imports on save
- Install Lombok plugin for your IDE
- Set Maven to download sources

## Git Workflow

- **Main Branch:** `develop`
- **Production Branch:** `master`
- **Versioning:** `${revision}` = `2.8.0-SNAPSHOT` (CI-friendly, resolved by flatten-maven-plugin)
- **Branch naming:** `feature/JNG-XXX_description`, `bugfix/JNG-XXX_description`, `hotfix/JNG-XXX_description`
- **Every commit must reference a JIRA ticket** in `JNG-XXX` format
- **Issue tracker:** [JIRA](https://blackbelt.atlassian.net/jira/dashboards)

## Important Notes

1. **Epsilon gotcha — ETL guard inheritance:** Guards are NOT inherited by child ETL rules. You must repeat guards in every `extends` rule.
2. **Epsilon gotcha — model caching:** Set `cached(false)` on `EmfModelContext` when EOL operations return non-constant values during EGL/EGX generation, or you will get stale cached results.
3. **Epsilon gotcha — equivalent() in guards:** The `equivalent()` function cannot be used inside ETL rule guards because equivalent objects don't exist yet at guard evaluation time.
4. **Builder pattern everywhere:** All major classes use Lombok `@Builder`. Construct via `executionContextBuilder()`, `emfModelContextBuilder()`, `etlExecutionContextBuilder()`, etc.
5. **AutoCloseable lifecycle:** `ExecutionContext` implements `AutoCloseable`. Always call `load()` → `executeProgram()` → `commit()` → `close()` (or use try-with-resources).
6. **OSGi dual-targeting:** Code must work in both standalone Maven and OSGi/Karaf environments. The `epsilon-runtime-osgi` module adds `BundleURIHandler` for OSGi resource resolution.
7. **Test JVM args:** Tests require `--add-opens` flags for Java 21 module access (configured in parent POM's surefire plugin).

## Related Documentation

- [README.md](README.md) — Project overview with architecture diagrams and full transformation example
- [CONTRIBUTING.md](CONTRIBUTING.md) — Development setup and contribution guidelines
- [epsilon-development.md](epsilon-development.md) — Epsilon framework gotchas and pitfalls
- [.github/CIFLOW.md](.github/CIFLOW.md) — CI/CD pipeline and branching strategy with flow diagrams
- [Epsilon Book](https://www.eclipse.org/epsilon/doc/book/) — Official Eclipse Epsilon documentation
