# eol-patch Specification

## Purpose

The eol-patch module provides patched versions of internal Eclipse Epsilon classes to improve performance and enhance reflection capabilities when running Epsilon outside the Eclipse IDE.

## Architecture

Two patched classes that replace their Epsilon originals on the classpath:
- `EolModelElementTypeNotFoundException` (package: `org.eclipse.epsilon.eol.exceptions.models`) — suppresses stack trace generation for performance
- `ReflectionUtil` (package: `org.eclipse.epsilon.eol.util`) — enhanced reflection utility with better method discovery, autoboxing support, and public class traversal

## Requirements

### Requirement: Suppress stack traces for type-not-found exceptions

The patched `EolModelElementTypeNotFoundException` SHALL suppress stack trace printing to avoid performance overhead in scenarios where this exception is thrown frequently as part of normal Epsilon type resolution.

#### Scenario: Stack trace suppressed
- **GIVEN** an `EolModelElementTypeNotFoundException` is thrown during model element type resolution
- **WHEN** `printStackTrace()` is called on the exception
- **THEN** no output is produced (the method is a no-op)

### Requirement: Enhanced reflection for method discovery

The patched `ReflectionUtil` SHALL discover and invoke methods across all public classes and interfaces in an object's type hierarchy, supporting autoboxing and varargs matching.

#### Scenario: Discover methods from public interfaces
- **GIVEN** an object whose class implements a public interface with method `getName()`
- **WHEN** `ReflectionUtil.getMethodsFromPublicClassesForName(object, "getName")` is called
- **THEN** the method is found even if the object's declaring class is not public

#### Scenario: Method invocation with autoboxing
- **GIVEN** a method that accepts `int` and an `Integer` argument
- **WHEN** `ReflectionUtil.executeMethod(object, "method", Integer.valueOf(42))` is called
- **THEN** the method is found via autoboxing and executed successfully

### Requirement: Public class discovery

`ReflectionUtil.discoverPublicClasses(clazz)` SHALL traverse the class hierarchy and all implemented interfaces to find all public types, enabling method resolution on non-public implementation classes.

#### Scenario: Discover public superclasses
- **GIVEN** a non-public class that extends `AbstractList` and implements `Serializable`
- **WHEN** `ReflectionUtil.discoverPublicClasses(clazz)` is called
- **THEN** the result includes `AbstractList`, `List`, `Collection`, `Iterable`, `Serializable`, and `Object`
