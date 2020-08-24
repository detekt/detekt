---
title: Naming Rule Set
sidebar: home_sidebar
keywords: rules, naming
permalink: naming.html
toc: true
folder: documentation
---
The naming ruleset contains rules which assert the naming of different parts of the codebase.

### ClassNaming

Reports when class or object names which do not follow the specified naming convention are used.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``classPattern`` (default: ``'[A-Z][a-zA-Z0-9]*'``)

   naming pattern

### ConstructorParameterNaming

Reports constructor parameter names which do not follow the specified naming convention are used.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``parameterPattern`` (default: ``'[a-z][A-Za-z0-9]*'``)

   naming pattern

* ``privateParameterPattern`` (default: ``'[a-z][A-Za-z0-9]*'``)

   naming pattern

* ``excludeClassPattern`` (default: ``'$^'``)

   ignores variables in classes which match this regex

* ``ignoreOverridden`` (default: ``true``)

   ignores constructor properties that have the override modifier

### EnumNaming

Reports when enum names which do not follow the specified naming convention are used.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``enumEntryPattern`` (default: ``'[A-Z][_a-zA-Z0-9]*'``)

   naming pattern

### ForbiddenClassName

Reports class names which are forbidden per configuration.
By default this rule does not report any classes.
Examples for forbidden names might be too generic class names like `...Manager`.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``forbiddenName`` (default: ``[]``)

   forbidden class names

### FunctionMaxLength

Reports when very long function names are used.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``maximumFunctionNameLength`` (default: ``30``)

   maximum name length

### FunctionMinLength

Reports when very short function names are used.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``minimumFunctionNameLength`` (default: ``3``)

   minimum name length

### FunctionNaming

Reports when function names which do not follow the specified naming convention are used.
One exception are factory functions used to create instances of classes.
These factory functions can have the same name as the class being created.

**Severity**: Style

**Debt**: 5min

**Aliases**: FunctionName

#### Configuration options:

* ``functionPattern`` (default: ``'([a-z][a-zA-Z0-9]*)|(`.*`)'``)

   naming pattern

* ``excludeClassPattern`` (default: ``'$^'``)

   ignores functions in classes which match this regex

* ``ignoreOverridden`` (default: ``true``)

   ignores functions that have the override modifier

* ``ignoreAnnotated`` (default: ``['Composable']``)

   ignore naming for functions in the context of these
annotation class names

### FunctionParameterNaming

Reports function parameter names which do not follow the specified naming convention are used.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``parameterPattern`` (default: ``'[a-z][A-Za-z0-9]*'``)

   naming pattern

* ``excludeClassPattern`` (default: ``'$^'``)

   ignores variables in classes which match this regex

* ~~``ignoreOverriddenFunctions``~~ (default: ``true``)

   **Deprecated**: Use `ignoreOverridden` instead

   ignores overridden functions with parameters not matching the pattern

* ``ignoreOverridden`` (default: ``true``)

   ignores overridden functions with parameters not matching the pattern

### InvalidPackageDeclaration

Reports when the package declaration is missing or the file location does not match the declared package.

**Severity**: Maintainability

**Debt**: 5min

#### Configuration options:

* ``rootPackage`` (default: ``''``)

   if specified this part of the package structure is ignored

### MatchingDeclarationName

"If a Kotlin file contains a single non-private class (potentially with related top-level declarations),
its name should be the same as the name of the class, with the .kt extension appended.
If a file contains multiple classes, or only top-level declarations,
choose a name describing what the file contains, and name the file accordingly.
Use camel humps with an uppercase first letter (e.g. ProcessDeclarations.kt).

The name of the file should describe what the code in the file does.
Therefore, you should avoid using meaningless words such as "Util" in file names." - Official Kotlin Style Guide

More information at: http://kotlinlang.org/docs/reference/coding-conventions.html

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``mustBeFirst`` (default: ``true``)

   name should only be checked if the file starts with a class or object

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

**Severity**: Style

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

### NonBooleanPropertyPrefixedWithIs

Reports when property with 'is' prefix doesn't have a boolean type.
Please check the [chapter 8.3.2 at Java Language Specification](https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.3.2)

**Requires Type Resolution**

**Severity**: Warning

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

Reports when property names inside objects which do not follow the specified naming convention are used.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``constantPattern`` (default: ``'[A-Za-z][_A-Za-z0-9]*'``)

   naming pattern

* ``propertyPattern`` (default: ``'[A-Za-z][_A-Za-z0-9]*'``)

   naming pattern

* ``privatePropertyPattern`` (default: ``'(_)?[A-Za-z][_A-Za-z0-9]*'``)

   naming pattern

### PackageNaming

Reports when package names which do not follow the specified naming convention are used.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``packagePattern`` (default: ``'[a-z]+(\.[a-z][A-Za-z0-9]*)*'``)

   naming pattern

### TopLevelPropertyNaming

Reports when top level constant names which do not follow the specified naming convention are used.

**Severity**: Style

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

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``maximumVariableNameLength`` (default: ``64``)

   maximum name length

### VariableMinLength

Reports when very short variable names are used.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``minimumVariableNameLength`` (default: ``1``)

   maximum name length

### VariableNaming

Reports when variable names which do not follow the specified naming convention are used.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``variablePattern`` (default: ``'[a-z][A-Za-z0-9]*'``)

   naming pattern

* ``privateVariablePattern`` (default: ``'(_)?[a-z][A-Za-z0-9]*'``)

   naming pattern

* ``excludeClassPattern`` (default: ``'$^'``)

   ignores variables in classes which match this regex

* ``ignoreOverridden`` (default: ``true``)

   ignores member properties that have the override modifier
