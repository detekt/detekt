---
title: Naming Rule Set
sidebar: home_sidebar
keywords: [rules, naming]
permalink: naming.html
toc: true
folder: documentation
---
Rule Set ID: `naming`

The naming ruleset contains rules which assert the naming of different parts of the codebase.

### BooleanPropertyNaming

Reports boolean property names that do not follow the specified naming convention.

**Active by default**: No

**Requires Type Resolution**

#### Configuration options:

* ``allowedPattern`` (default: ``'^(is|has|are)'``)

  naming pattern

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

**Aliases**: ClassName

#### Configuration options:

* ``classPattern`` (default: ``'[A-Z][a-zA-Z0-9]*'``)

  naming pattern

### ConstructorParameterNaming

Reports constructor parameter names that do not follow the specified naming convention.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``parameterPattern`` (default: ``'[a-z][A-Za-z0-9]*'``)

  naming pattern

* ``privateParameterPattern`` (default: ``'[a-z][A-Za-z0-9]*'``)

  naming pattern

* ``excludeClassPattern`` (default: ``'$^'``)

  ignores variables in classes which match this regex

### EnumNaming

Reports enum names that do not follow the specified naming convention.

**Active by default**: Yes - Since v1.0.0

**Aliases**: EnumEntryName

#### Configuration options:

* ``enumEntryPattern`` (default: ``'[A-Z][_a-zA-Z0-9]*'``)

  naming pattern

### ForbiddenClassName

Reports class names which are forbidden per configuration. By default, this rule does not report any classes.
This can be used to prevent the use of overly generic class names like `*Manager` or names shadowing common
types like `LocalDate`.

**Active by default**: No

#### Configuration options:

* ``forbiddenName`` (default: ``[]``)

  List of glob patterns to be disallowed as class names

### FunctionNameMaxLength

Reports when very long function names are used.

**Active by default**: No

**Aliases**: FunctionMaxNameLength

#### Configuration options:

* ``maximumFunctionNameLength`` (default: ``30``)

  maximum name length

### FunctionNameMinLength

Reports when very short function names are used.

**Active by default**: No

**Aliases**: FunctionMinNameLength

#### Configuration options:

* ``minimumFunctionNameLength`` (default: ``3``)

  minimum name length

### FunctionNaming

Reports function names that do not follow the specified naming convention.
One exception are factory functions used to create instances of classes.
These factory functions can have the same name as the class being created.

**Active by default**: Yes - Since v1.0.0

**Aliases**: FunctionName

#### Configuration options:

* ``functionPattern`` (default: ``'[a-z][a-zA-Z0-9]*'``)

  naming pattern

* ``excludeClassPattern`` (default: ``'$^'``)

  ignores functions in classes which match this regex

### FunctionParameterNaming

Reports function parameter names that do not follow the specified naming convention.

**Active by default**: Yes - Since v1.0.0

#### Configuration options:

* ``parameterPattern`` (default: ``'[a-z][A-Za-z0-9]*'``)

  naming pattern

* ``excludeClassPattern`` (default: ``'$^'``)

  ignores variables in classes which match this regex

### InvalidPackageDeclaration

Reports when the file location does not match the declared package.

**Active by default**: Yes - Since v1.21.0

**Aliases**: PackageDirectoryMismatch

#### Configuration options:

* ``rootPackage`` (default: ``''``)

  if specified this part of the package structure is ignored

* ``requireRootInDeclaration`` (default: ``false``)

  requires the declaration to start with the specified rootPackage

### LambdaParameterNaming

Reports lambda parameter names that do not follow the specified naming convention.

**Active by default**: No

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

This rule reports a member that has the same name as the containing class or object.
This might result in confusion.
The member should either be renamed or changed to a constructor.
Factory functions that create an instance of the class are exempt from this rule.

**Active by default**: Yes - Since v1.2.0

**Requires Type Resolution**

#### Configuration options:

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
Shadowing makes it impossible to access a variable with the same name in the scope
except of underscore lambda parameters for unused variables.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

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
    listOf(1).map { _ -> println("_") }
}
```

### NonBooleanPropertyPrefixedWithIs

Reports when property with 'is' prefix doesn't have a boolean type.
Please check [chapter 8.3.2 on the Java Language Specification](https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.3.2)

**Active by default**: No

**Requires Type Resolution**

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

**Aliases**: ObjectPropertyName

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

**Aliases**: PackageName

#### Configuration options:

* ``packagePattern`` (default: ``'[a-z]+(\.[a-z][A-Za-z0-9]*)*'``)

  naming pattern

### TopLevelPropertyNaming

Reports top level property names that do not follow the specified naming convention.

**Active by default**: Yes - Since v1.0.0

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

#### Configuration options:

* ``maximumVariableNameLength`` (default: ``64``)

  maximum name length

### VariableMinLength

Reports when very short variable names are used.

**Active by default**: No

#### Configuration options:

* ``minimumVariableNameLength`` (default: ``1``)

  minimum name length

### VariableNaming

Reports variable names that do not follow the specified naming convention.

**Active by default**: Yes - Since v1.0.0

**Aliases**: PropertyName

#### Configuration options:

* ``variablePattern`` (default: ``'[a-z][A-Za-z0-9]*'``)

  naming pattern

* ``privateVariablePattern`` (default: ``'(_)?[a-z][A-Za-z0-9]*'``)

  naming pattern

* ``excludeClassPattern`` (default: ``'$^'``)

  ignores variables in classes which match this regex
