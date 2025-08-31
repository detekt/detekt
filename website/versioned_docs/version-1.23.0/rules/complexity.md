---
title: Complexity Rule Set
sidebar: home_sidebar
keywords: [rules, complexity]
permalink: complexity.html
toc: true
folder: documentation
---
This rule set contains rules that report complex code.

### CognitiveComplexMethod

Complex methods are hard to understand and read. It might not be obvious what side-effects a complex method has.
Prefer splitting up complex methods into smaller methods that are in turn easier to understand.
Smaller methods can also be named much clearer which leads to improved readability of the code.

This rule measures and restricts the complexity of the method through the [Cognitive Complexity metric of Sonasource](https://www.sonarsource.com/docs/CognitiveComplexity.pdf).
Which improves McCabe's Cyclomatic Complexity (see [CyclomaticComplexMethod](complexity.md#cyclomaticcomplexmethod)) considering the programmer's mental model.

Similar to cyclomatic complexity, it is a mathematical model that increases +1 complexity for flow control statements,
but increases additional complexity when the statements are deeply nested.

The statements that increase the complexity or the nesting level are as follows.
- __Complexity Increments__ - `if`, `when`, `for`, `while`, `do while`, `catch`, `labeled break`, `labeled continue`, `labeled return`, `recursion call`, `&&`, `||`
- __Nesting Level Increments__ - `if`, `when`, `for`, `while`, `do while`, `catch`, `nested function`
- __Additional Complexity Increments by Nesting Level__ - `if`, `when`, `for`, `while`, `do while`, `catch`

**Active by default**: No

**Debt**: 20min

#### Configuration options:

* ``threshold`` (default: ``15``)

  Cognitive Complexity number for a method.

### ComplexCondition

Complex conditions make it hard to understand which cases lead to the condition being true or false. To improve
readability and understanding of complex conditions consider extracting them into well-named functions or variables
and call those instead.

**Active by default**: Yes - Since v1.0.0

**Debt**: 20min

#### Configuration options:

* ``threshold`` (default: ``4``)

  the number of conditions which will trigger the rule

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

**Active by default**: No

**Debt**: 20min

#### Configuration options:

* ``threshold`` (default: ``10``)

  the amount of definitions in an interface to trigger the rule

* ``includeStaticDeclarations`` (default: ``false``)

  whether static declarations should be included

* ``includePrivateDeclarations`` (default: ``false``)

  whether private declarations should be included

* ``ignoreOverloaded`` (default: ``false``)

  ignore overloaded methods - only count once

### CyclomaticComplexMethod

Complex methods are hard to understand and read. It might not be obvious what side-effects a complex method has.
Prefer splitting up complex methods into smaller methods that are in turn easier to understand.
Smaller methods can also be named much clearer which leads to improved readability of the code.

This rule uses McCabe's Cyclomatic Complexity (MCC) metric to measure the number of
linearly independent paths through a function's source code (https://www.ndepend.com/docs/code-metrics#CC).
The higher the number of independent paths, the more complex a method is.
Complex methods use too many of the following statements.
Each one of them adds one to the complexity count.

- __Conditional statements__ - `if`, `else if`, `when`
- __Jump statements__ - `continue`, `break`
- __Loops__ - `for`, `while`, `do-while`, `forEach`
- __Operators__ `&&`, `||`, `?:`
- __Exceptions__ - `catch`, `use`
- __Scope Functions__ - `let`, `run`, `with`, `apply`, and `also` ->
[Reference](https://kotlinlang.org/docs/scope-functions.html)

**Active by default**: Yes - Since v1.0.0

**Debt**: 20min

**Aliases**: ComplexMethod

#### Configuration options:

* ``threshold`` (default: ``15``)

  McCabe's Cyclomatic Complexity (MCC) number for a method.

* ``ignoreSingleWhenExpression`` (default: ``false``)

  Ignores a complex method if it only contains a single when expression.

* ``ignoreSimpleWhenEntries`` (default: ``false``)

  Whether to ignore simple (braceless) when entries.

* ``ignoreNestingFunctions`` (default: ``false``)

  Whether to ignore functions which are often used instead of an `if` or `for` statement.

* ``nestingFunctions`` (default: ``['also', 'apply', 'forEach', 'isNotNull', 'ifNull', 'let', 'run', 'use', 'with']``)

  Comma separated list of function names which add complexity.

### LabeledExpression

This rule reports labeled expressions. Expressions with labels generally increase complexity and worsen the
maintainability of the code. Refactor the violating code to not use labels instead.
Labeled expressions referencing an outer class with a label from an inner class are allowed, because there is no
way to get the instance of an outer class from an inner class in Kotlin.

**Active by default**: No

**Debt**: 20min

#### Configuration options:

* ``ignoredLabels`` (default: ``[]``)

  allows to provide a list of label names which should be ignored by this rule

#### Noncompliant Code:

```kotlin
val range = listOf<String>("foo", "bar")
loop@ for (r in range) {
    if (r == "bar") break@loop
    println(r)
}

class Outer {
    inner class Inner {
        fun f() {
            val i = this@Inner // referencing itself, use `this instead
        }
    }
}
```

#### Compliant Code:

```kotlin
val range = listOf<String>("foo", "bar")
for (r in range) {
    if (r == "bar") break
    println(r)
}

class Outer {
    inner class Inner {
        fun f() {
            val outer = this@Outer
        }
        fun Int.extend() {
            val inner = this@Inner // this would reference Int and not Inner
        }
    }
}
```

### LargeClass

This rule reports large classes. Classes should generally have one responsibility. Large classes can indicate that
the class does instead handle multiple responsibilities. Instead of doing many things at once prefer to
split up large classes into smaller classes. These smaller classes are then easier to understand and handle less
things.

**Active by default**: Yes - Since v1.0.0

**Debt**: 20min

#### Configuration options:

* ``threshold`` (default: ``600``)

  the size of class required to trigger the rule

### LongMethod

Methods should have one responsibility. Long methods can indicate that a method handles too many cases at once.
Prefer smaller methods with clear names that describe their functionality clearly.

Extract parts of the functionality of long methods into separate, smaller methods.

**Active by default**: Yes - Since v1.0.0

**Debt**: 20min

#### Configuration options:

* ``threshold`` (default: ``60``)

  number of lines in a method to trigger the rule

### LongParameterList

Reports functions and constructors which have more parameters than a certain threshold.

**Active by default**: Yes - Since v1.0.0

**Debt**: 20min

#### Configuration options:

* ~~``threshold``~~ (default: ``6``)

  **Deprecated**: Use `functionThreshold` and `constructorThreshold` instead

  number of parameters required to trigger the rule

* ``functionThreshold`` (default: ``6``)

  number of function parameters required to trigger the rule

* ``constructorThreshold`` (default: ``7``)

  number of constructor parameters required to trigger the rule

* ``ignoreDefaultParameters`` (default: ``false``)

  ignore parameters that have a default value

* ``ignoreDataClasses`` (default: ``true``)

  ignore long constructor parameters list for data classes

* ``ignoreAnnotatedParameter`` (default: ``[]``)

  ignore the annotated parameters for the count (e.g. `fun foo(@Value bar: Int)` would not be counted

### MethodOverloading

This rule reports methods which are overloaded often.
Method overloading tightly couples these methods together which might make the code harder to understand.

Refactor these methods and try to use optional parameters instead to prevent some of the overloading.

**Active by default**: No

**Debt**: 20min

#### Configuration options:

* ``threshold`` (default: ``6``)

  number of overloads which will trigger the rule

### NamedArguments

Reports function invocations which have more arguments than a certain threshold and are all not named. Calls with
too many arguments are more difficult to understand so a named arguments help.

**Active by default**: No

**Requires Type Resolution**

**Debt**: 5min

#### Configuration options:

* ``threshold`` (default: ``3``)

  number of arguments that triggers this inspection

* ``ignoreArgumentsMatchingNames`` (default: ``false``)

  ignores when argument values are the same as the parameter names

#### Noncompliant Code:

```kotlin
fun sum(a: Int, b: Int, c: Int, d: Int) {
}
sum(1, 2, 3, 4)
```

#### Compliant Code:

```kotlin
fun sum(a: Int, b: Int, c: Int, d: Int) {
}
sum(a = 1, b = 2, c = 3, d = 4)
```

### NestedBlockDepth

This rule reports excessive nesting depth in functions. Excessively nested code becomes harder to read and increases
its hidden complexity. It might become harder to understand edge-cases of the function.

Prefer extracting the nested code into well-named functions to make it easier to understand.

**Active by default**: Yes - Since v1.0.0

**Debt**: 20min

#### Configuration options:

* ``threshold`` (default: ``4``)

  the nested depth required to trigger rule

### NestedScopeFunctions

Although the scope functions are a way of making the code more concise, avoid overusing them: it can decrease
your code readability and lead to errors. Avoid nesting scope functions and be careful when chaining them:
it's easy to get confused about the current context object and the value of this or it.

[Reference](https://kotlinlang.org/docs/scope-functions.html)

**Active by default**: No

**Requires Type Resolution**

**Debt**: 5min

#### Configuration options:

* ``threshold`` (default: ``1``)

  Number of nested scope functions allowed.

* ``functions`` (default: ``['kotlin.apply', 'kotlin.run', 'kotlin.with', 'kotlin.let', 'kotlin.also']``)

  Set of scope function names which add complexity. Function names have to be fully qualified. For example 'kotlin.apply'.

#### Noncompliant Code:

```kotlin
// Try to figure out, what changed, without knowing the details
first.apply {
    second.apply {
        b = a
        c = b
    }
}
```

#### Compliant Code:

```kotlin
// 'a' is a property of current class
// 'b' is a property of class 'first'
// 'c' is a property of class 'second'
first.b = this.a
second.c = first.b
```

### ReplaceSafeCallChainWithRun

Chains of safe calls on non-nullable types are redundant and can be removed by enclosing the redundant safe calls in
a `run {}` block. This improves code coverage and reduces cyclomatic complexity as redundant null checks are removed.

This rule only checks from the end of a chain and works backwards, so it won't recommend inserting run blocks in the
middle of a safe call chain as that is likely to make the code more difficult to understand.

The rule will check for every opportunity to replace a safe call when it sits at the end of a chain, even if there's
only one, as that will still improve code coverage and reduce cyclomatic complexity.

**Active by default**: No

**Requires Type Resolution**

**Debt**: 10min

#### Noncompliant Code:

```kotlin
val x = System.getenv()
    ?.getValue("HOME")
    ?.toLowerCase()
    ?.split("/") ?: emptyList()
```

#### Compliant Code:

```kotlin
val x = getenv()?.run {
    getValue("HOME")
        .toLowerCase()
        .split("/")
} ?: emptyList()
```

### StringLiteralDuplication

This rule detects and reports duplicated String literals. Repeatedly typing out the same String literal across the
codebase makes it harder to change and maintain.

Instead, prefer extracting the String literal into a property or constant.

**Active by default**: No

**Debt**: 5min

#### Configuration options:

* ``threshold`` (default: ``3``)

  amount of duplications to trigger rule

* ``ignoreAnnotation`` (default: ``true``)

  if values in Annotations should be ignored

* ``excludeStringsWithLessThan5Characters`` (default: ``true``)

  if short strings should be excluded

* ``ignoreStringsRegex`` (default: ``'$^'``)

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

**Active by default**: Yes - Since v1.0.0

**Debt**: 20min

#### Configuration options:

* ``thresholdInFiles`` (default: ``11``)

  threshold in files

* ``thresholdInClasses`` (default: ``11``)

  threshold in classes

* ``thresholdInInterfaces`` (default: ``11``)

  threshold in interfaces

* ``thresholdInObjects`` (default: ``11``)

  threshold in objects

* ``thresholdInEnums`` (default: ``11``)

  threshold in enums

* ``ignoreDeprecated`` (default: ``false``)

  ignore deprecated functions

* ``ignorePrivate`` (default: ``false``)

  ignore private functions

* ``ignoreOverridden`` (default: ``false``)

  ignore overridden functions
