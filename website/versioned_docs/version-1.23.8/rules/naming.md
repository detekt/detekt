---
title: Naming Rule Set
sidebar: home_sidebar
keywords: [rules, naming]
permalink: naming.html
toc: true
folder: documentation
---
The naming ruleset contains rules which assert the naming of different parts of the codebase.

### BooleanPropertyNaming

Reports when a boolean property doesn't match a pattern

**Active by default**: No

**Requires Type Resolution**

**Debt**: 5min

#### Configuration options:

* ``allowedPattern`` (default: ``'^(is|has|are)'``)

  naming pattern

* ~~``ignoreOverridden``~~ (default: ``true``)

  **Deprecated**: This configuration is ignored and will be removed in the future

  ignores properties that have the override modifier

#### Noncompliant Code:

```kotlin
val progressBar: Boolean = true
```

#### Compliant Code:

```kotlin
val hasProgressBar: Boolean = true
```

### ClassNaming

Reports class or object names that do not follow the specified naming convention.

**Active by default**: Yes - Since v1.0.0

**Debt**: 5min

**Aliases**: ClassName

#### Configuration options:

* ``classPattern`` (default: ``'[A-Z][a-zA-Z0-9]*'``)

  naming pattern

### ConstructorParameterNaming

Reports constructor parameter names that do not follow the specified naming convention.

**Active by default**: Yes - Since v1.0.0

**Debt**: 5min

#### Configuration options:

* ``parameterPattern`` (default: ``'[a-z][A-Za-z0-9]*'``)

  naming pattern

* ``privateParameterPattern`` (default: ``'[a-z][A-Za-z0-9]*'``)

  naming pattern

* ``excludeClassPattern`` (default: ``'$^'``)

  ignores variables in classes which match this regex

* ~~``ignoreOverridden``~~ (default: ``true``)

  **Deprecated**: This configuration is ignored and will be removed in the future

  ignores constructor properties that have the override modifier

### EnumNaming

Reports enum names that do not follow the specified naming convention.

**Active by default**: Yes - Since v1.0.0

**Debt**: 5min

#### Configuration options:

* ``enumEntryPattern`` (default: ``'[A-Z][_a-zA-Z0-9]*'``)

  naming pattern

### ForbiddenClassName

Reports class names which are forbidden per configuration. By default, this rule does not report any classes.
Examples for forbidden names might be too generic class names like `...Manager`.

**Active by default**: No

**Debt**: 5min

#### Configuration options:

* ``forbiddenName`` (default: ``[]``)

  forbidden class names

### FunctionMaxLength

Reports when very long function names are used.

**Active by default**: No

**Debt**: 5min

#### Configuration options:

* ``maximumFunctionNameLength`` (default: ``30``)

  maximum name length

### FunctionMinLength

Reports when very short function names are used.

**Active by default**: No

**Debt**: 5min

#### Configuration options:

* ``minimumFunctionNameLength`` (default: ``3``)

  minimum name length

### FunctionNaming

Reports function names that do not follow the specified naming convention.
One exception are factory functions used to create instances of classes.
These factory functions can have the same name as the class being created.

**Active by default**: Yes - Since v1.0.0

**Debt**: 5min

**Aliases**: FunctionName

#### Configuration options:

* ``functionPattern`` (default: ``'[a-z][a-zA-Z0-9]*'``)

  naming pattern

* ``excludeClassPattern`` (default: ``'$^'``)

  ignores functions in classes which match this regex

* ~~``ignoreOverridden``~~ (default: ``true``)

  **Deprecated**: This configuration is ignored and will be removed in the future

  ignores functions that have the override modifier

### FunctionParameterNaming

Reports function parameter names that do not follow the specified naming convention.

**Active by default**: Yes - Since v1.0.0

**Debt**: 5min

#### Configuration options:

* ``parameterPattern`` (default: ``'[a-z][A-Za-z0-9]*'``)

  naming pattern

* ``excludeClassPattern`` (default: ``'$^'``)

  ignores variables in classes which match this regex

* ~~``ignoreOverriddenFunctions``~~ (default: ``true``)

  **Deprecated**: Use `ignoreOverridden` instead

  ignores overridden functions with parameters not matching the pattern

* ~~``ignoreOverridden``~~ (default: ``true``)

  **Deprecated**: This configuration is ignored and will be removed in the future

  ignores overridden functions with parameters not matching the pattern

### InvalidPackageDeclaration

Reports when the file location does not match the declared package.

**Active by default**: Yes - Since v1.21.0

**Debt**: 5min

**Aliases**: PackageDirectoryMismatch

#### Configuration options:

* ``rootPackage`` (default: ``''``)

  if specified this part of the package structure is ignored

* ``requireRootInDeclaration`` (default: ``false``)

  requires the declaration to start with the specified rootPackage

### LambdaParameterNaming

Reports lambda parameter names that do not follow the specified naming convention.

**Active by default**: No

**Debt**: 5min

#### Configuration options:

* ``parameterPattern`` (default: ``'[a-z][A-Za-z0-9]*|_'``)

  naming pattern

### MatchingDeclarationName

"If a Kotlin file contains a single non-private class (potentially with related top-level declarations),
its name should be the same as the name of the class, with the .kt extension appended.
If a file contains multiple classes, or only top-level declarations,
choose a name describing what the file contains, and name the file accordingly.
Use camel humps with an uppercase first letter (e.g. ProcessDeclarations.kt).

The name of the file should describe what the code in the file does.
Therefore, you should avoid using meaningless words such as "Util" in file names." - Official Kotlin Style Guide

More information at: https://kotlinlang.org/docs/coding-conventions.html

**Active by default**: Yes - Since v1.0.0

**Debt**: 5min

#### Configuration options:

* ``mustBeFirst`` (default: ``true``)

  name should only be checked if the file starts with a class or object

* ``multiplatformTargets`` (default: ``['ios', 'android', 'js', 'jvm', 'native', 'iosArm64', 'iosX64', 'macosX64', 'mingwX64', 'linuxX64']``)

  kotlin multiplatform targets, used to allow file names like `MyClass.jvm.kt`

#### Noncompliant Code:

```kotlin
class Foo // FooUtils.kt

fun Bar.toFoo(): Foo = ...
fun Foo.toBar(): Bar = ...
```

#### Compliant Code:

```kotlin
class Foo { // Foo.kt
    fun stuff() = 42
}

fun Bar.toFoo(): Foo = ...
```

### MemberNameEqualsClassName

This rule reports a member that has the same as the containing class or object.
This might result in confusion.
The member should either be renamed or changed to a constructor.
Factory functions that create an instance of the class are exempt from this rule.

**Active by default**: Yes - Since v1.2.0

**Debt**: 5min

#### Configuration options:

* ~~``ignoreOverriddenFunction``~~ (default: ``true``)

  **Deprecated**: Use `ignoreOverridden` instead

  if overridden functions and properties should be ignored

* ``ignoreOverridden`` (default: ``true``)

  if overridden functions and properties should be ignored

#### Noncompliant Code:

```kotlin
class MethodNameEqualsClassName {

    fun methodNameEqualsClassName() { }
}

class PropertyNameEqualsClassName {

    val propertyEqualsClassName = 0
}
```

#### Compliant Code:

```kotlin
class Manager {

    companion object {
        // factory functions can have the same name as the class
        fun manager(): Manager {
            return Manager()
        }
    }
}
```

### NoNameShadowing

Disallows shadowing variable declarations.
Shadowing makes it impossible to access a variable with the same name in the scope.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

**Debt**: 5min

#### Noncompliant Code:

```kotlin
fun test(i: Int, j: Int, k: Int) {
    val i = 1
    val (j, _) = 1 to 2
    listOf(1).map { k -> println(k) }
    listOf(1).forEach {
        listOf(2).forEach {
        }
    }
}
```

#### Compliant Code:

```kotlin
fun test(i: Int, j: Int, k: Int) {
    val x = 1
    val (y, _) = 1 to 2
    listOf(1).map { z -> println(z) }
    listOf(1).forEach {
        listOf(2).forEach { x ->
        }
    }
}
```

### NonBooleanPropertyPrefixedWithIs

Reports when property with 'is' prefix doesn't have a boolean type.
Please check the [chapter 8.3.2 at Java Language Specification](https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.3.2)

**Active by default**: No

**Requires Type Resolution**

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val isEnabled: Int = 500
```

#### Compliant Code:

```kotlin
val isEnabled: Boolean = false
```

### ObjectPropertyNaming

Reports property names inside objects that do not follow the specified naming convention.

**Active by default**: Yes - Since v1.0.0

**Debt**: 5min

#### Configuration options:

* ``constantPattern`` (default: ``'[A-Za-z][_A-Za-z0-9]*'``)

  naming pattern

* ``propertyPattern`` (default: ``'[A-Za-z][_A-Za-z0-9]*'``)

  naming pattern

* ``privatePropertyPattern`` (default: ``'(_)?[A-Za-z][_A-Za-z0-9]*'``)

  naming pattern

### PackageNaming

Reports package names that do not follow the specified naming convention.

**Active by default**: Yes - Since v1.0.0

**Debt**: 5min

**Aliases**: PackageName, PackageDirectoryMismatch

#### Configuration options:

* ``packagePattern`` (default: ``'[a-z]+(\.[a-z][A-Za-z0-9]*)*'``)

  naming pattern

### TopLevelPropertyNaming

Reports top level constant that which do not follow the specified naming convention.

**Active by default**: Yes - Since v1.0.0

**Debt**: 5min

#### Configuration options:

* ``constantPattern`` (default: ``'[A-Z][_A-Z0-9]*'``)

  naming pattern

* ``propertyPattern`` (default: ``'[A-Za-z][_A-Za-z0-9]*'``)

  naming pattern

* ``privatePropertyPattern`` (default: ``'_?[A-Za-z][_A-Za-z0-9]*'``)

  naming pattern

### VariableMaxLength

Reports when very long variable names are used.

**Active by default**: No

**Debt**: 5min

#### Configuration options:

* ``maximumVariableNameLength`` (default: ``64``)

  maximum name length

### VariableMinLength

Reports when very short variable names are used.

**Active by default**: No

**Debt**: 5min

#### Configuration options:

* ``minimumVariableNameLength`` (default: ``1``)

  minimum name length

### VariableNaming

Reports variable names that do not follow the specified naming convention.

**Active by default**: Yes - Since v1.0.0

**Debt**: 5min

#### Configuration options:

* ``variablePattern`` (default: ``'[a-z][A-Za-z0-9]*'``)

  naming pattern

* ``privateVariablePattern`` (default: ``'(_)?[a-z][A-Za-z0-9]*'``)

  naming pattern

* ``excludeClassPattern`` (default: ``'$^'``)

  ignores variables in classes which match this regex

* ~~``ignoreOverridden``~~ (default: ``true``)

  **Deprecated**: This configuration is ignored and will be removed in the future

  ignores member properties that have the override modifier
