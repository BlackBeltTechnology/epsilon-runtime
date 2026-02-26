# utils Specification

## Purpose

The utils module provides small, self-contained utility classes for string manipulation and hashing, used across the epsilon-runtime project and downstream consumers.

## Architecture

Three stateless utility classes in `hu.blackbelt.epsilon.runtime.utils`:
- `UUIDUtils` — UUID generation (v3 name-based and random)
- `MD5Utils` — MD5 hash computation
- `AbbreviateUtils` — String abbreviation by vowel removal

## Requirements

### Requirement: UUID v3 generation

`UUIDUtils.uuid3(namespace, name)` SHALL generate a deterministic UUID v3 from a namespace and name string, producing the same UUID for the same inputs.

#### Scenario: Deterministic UUID generation
- **GIVEN** a namespace `"ns"` and name `"test"`
- **WHEN** `UUIDUtils.uuid3("ns", "test")` is called twice
- **THEN** both calls return the same UUID string

### Requirement: Random UUID generation

`UUIDUtils.randomUUID()` SHALL generate a unique random UUID on each invocation.

#### Scenario: Unique UUIDs
- **WHEN** `UUIDUtils.randomUUID()` is called twice
- **THEN** the two results are different

### Requirement: MD5 hash computation

`MD5Utils.md5(input)` SHALL compute the MD5 hash of the given input string and return it as a hexadecimal string.

#### Scenario: Hash a string
- **GIVEN** the input string `"hello"`
- **WHEN** `MD5Utils.md5("hello")` is called
- **THEN** it returns the MD5 hex digest of `"hello"`

### Requirement: String abbreviation

`AbbreviateUtils.abbreviate(text, maxLength)` SHALL shorten a string to at most `maxLength` characters by progressively removing vowels.

#### Scenario: Abbreviate long string
- **GIVEN** the string `"EntityReference"` and max length `10`
- **WHEN** `AbbreviateUtils.abbreviate("EntityReference", 10)` is called
- **THEN** the result has at most 10 characters with vowels removed as needed

#### Scenario: Short string unchanged
- **GIVEN** the string `"ID"` and max length `10`
- **WHEN** `AbbreviateUtils.abbreviate("ID", 10)` is called
- **THEN** the result is `"ID"` unchanged
