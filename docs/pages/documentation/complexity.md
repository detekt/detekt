---
title: Complexity Rule Set
sidebar: home_sidebar
keywords: rules, complexity
permalink: complexity.html
toc: true
folder: documentation
---
This rule set contains rules that report complex code.

### ComplexCondition

Complex conditions make it hard to understand which cases lead to the condition being true or false. To improve
readability and understanding of complex conditions consider extracting them into well-named functions or variables
and call those instead.

**Severity**: Maintainability

**Debt**: 20min

#### Configuration options:

* `threshold` (default: `4`)

   

#### Noncompliant Code:

```kotlin
val str = "foo"
val isFoo = if (str.startsWith("foo") && !str.endsWith("foo") && !str.endsWith("bar") && !str.endsWith("_")) {
    // ...
}
```

#### Compliant Code:

```kotlin
val str = "foo"
val isFoo = if (str.startsWith("foo") && hasCorrectEnding()) {
    // ...
}

fun hasCorrectEnding() = return !str.endsWith("foo") && !str.endsWith("bar") && !str.endsWith("_")
```

### ComplexInterface

Complex interfaces which contain too many functions and/or properties indicate that this interface is handling too
many things at once. Interfaces should follow the single-responsibility principle to also encourage implementations
of this interface to not handle too many things at once.

Large interfaces should be split into smaller interfaces which have a clear responsibility and are easier
to understand and implement.

**Severity**: Maintainability

**Debt**: 20min

#### Configuration options:

* `threshold` (default: `10`)

   maximum amount of definitions in an interface

* `includeStaticDeclarations` (default: `false`)

   whether static declarations should be included

### ComplexMethod

Complex methods are hard to understand and read. It might not be obvious what side-effects a complex method has.
Prefer splitting up complex methods into smaller methods that are in turn easier to understand.
Smaller methods can also be named much clearer which leads to improved readability of the code.

**Severity**: Maintainability

**Debt**: 20min

#### Configuration options:

* `threshold` (default: `10`)

   MCC threshold for a method

* `ignoreSingleWhenExpression` (default: `false`)

   Ignores a complex method if it only contains a single when expression.

### LabeledExpression

This rule reports labeled expressions. Expressions with labels generally increase complexity and worsen the
maintainability of the code. Refactor the violating code to not use labels instead.

**Severity**: Maintainability

**Debt**: 20min

#### Noncompliant Code:

```kotlin
val range = listOf<String>("foo", "bar")
loop@ for (r in range) {
    if (r == "bar") break@loop
    println(r)
}
```

#### Compliant Code:

```kotlin
val range = listOf<String>("foo", "bar")
for (r in range) {
    if (r == "bar") break
    println(r)
}
```

### LargeClass

This rule reports large classes. Classes should generally have one responsibility. Large classes can indicate that
the class does instead handle multiple responsibilities. Instead of doing many things at once prefer to
split up large classes into smaller classes. These smaller classes are then easier to understand and handle less
things.

**Severity**: Maintainability

**Debt**: 20min

#### Configuration options:

* `threshold` (default: `150`)

   maximum size of a class

### LongMethod

Methods should have one responsibility. Long methods can indicate that a method handles too many cases at once.
Prefer smaller methods with clear names that describe their functionality clearly.

Extract parts of the functionality of long methods into separate, smaller methods.

**Severity**: Maintainability

**Debt**: 20min

#### Configuration options:

* `threshold` (default: `20`)

   maximum lines in a method

### LongParameterList

Reports functions which have more parameters than a certain threshold (default: 6).

**Severity**: Maintainability

**Debt**: 20min

#### Configuration options:

* `threshold` (default: `6`)

   maximum number of parameters

* `ignoreDefaultParameters` (default: `false`)

   ignore parameters that have a default value

### MethodOverloading

This rule reports methods which have many versions of the same method with different parameter overloading.
Method overloading tightly couples these methods together which might make the code harder to understand.

Refactor these methods and try to use optional parameters instead to prevent some of the overloading.

**Severity**: Maintainability

**Debt**: 20min

#### Configuration options:

* `threshold` (default: `6`)

   

### NestedBlockDepth

This rule reports excessive nesting depth in functions. Excessively nested code becomes harder to read and increases
its hidden complexity. It might become harder to understand edge-cases of the function.

Prefer extracting the nested code into well-named functions to make it easier to understand.

**Severity**: Maintainability

**Debt**: 20min

#### Configuration options:

* `threshold` (default: `4`)

   maximum nesting depth

### StringLiteralDuplication

This rule detects and reports duplicated String literals. Repeatedly typing out the same String literal across the
codebase makes it harder to change and maintain.

Instead, prefer extracting the String literal into a property or constant.

**Severity**: Maintainability

**Debt**: 5min

#### Configuration options:

* `threshold` (default: `3`)

   amount of duplications to trigger rule

* `ignoreAnnotation` (default: `true`)

   if values in Annotations should be ignored

* `excludeStringsWithLessThan5Characters` (default: `true`)

   if short strings should be excluded

* `ignoreStringsRegex` (default: `'$^'`)

   RegEx of Strings that should be ignored

#### Noncompliant Code:

```kotlin
class Foo {

    val s1 = "lorem"
    fun bar(s: String = "lorem") {
        s1.equals("lorem")
    }
}
```

#### Compliant Code:

```kotlin
class Foo {
    val lorem = "lorem"
    val s1 = lorem
    fun bar(s: String = lorem) {
        s1.equals(lorem)
    }
}
```

### TooManyFunctions

This rule reports files, classes, interfaces, objects and enums which contain too many functions.
Each element can be configured with different thresholds.

Too many functions indicate a violation of the single responsibility principle. Prefer extracting functionality
which clearly belongs together in separate parts of the code.

**Severity**: Maintainability

**Debt**: 20min

#### Configuration options:

* `thresholdInFiles` (default: `11`)

   threshold in files

* `thresholdInClasses` (default: `11`)

   threshold in classes

* `thresholdInInterfaces` (default: `11`)

   threshold in interfaces

* `thresholdInObjects` (default: `11`)

   threshold in objects

* `thresholdInEnums` (default: `11`)

   threshold in enums
