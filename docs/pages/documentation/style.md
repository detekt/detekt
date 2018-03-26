---
title: Style Rule Set
sidebar: home_sidebar
keywords: rules, style
permalink: style.html
toc: true
folder: documentation
---
The Style ruleset provides rules that assert the style of the code.
This will help keep code in line with the given
code style guidelines.

### CollapsibleIfStatements

This rule detects `if` statements which can be collapsed. This can reduce nesting and help improve readability.

However it should be carefully considered if merging the if statements actually does improve readability or if it
hides some edge-cases from the reader.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val i = 1
if (i > 0) {
    if (i < 5) {
        println(i)
    }
}
```

#### Compliant Code:

```kotlin
val i = 1
if (i > 0 && i < 5) {
    println(i)
}
```

### DataClassContainsFunctions

This rule reports functions inside data classes which have not been whitelisted as a conversion function.

Data classes should mainly be used to store data. This rule assumes that they should not contain any extra functions
aside functions that help with converting objects from/to one another.
Data classes will automatically have a generated `equals`, `toString` and `hashCode` function by the compiler.

**Severity**: Style

**Debt**: 20min

#### Configuration options:

* `conversionFunctionPrefix` (default: `'to'`)

   allowed conversion function names

#### Noncompliant Code:

```kotlin
data class DataClassWithFunctions(val i: Int) {
    fun foo() { }
}
```

### EqualsNullCall

To compare an object with `null` prefer using `==`. This rule detects and reports instances in the code where the
`equals()` method is used to compare a value with `null`.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
fun isNull(str: String) = str.equals(null)
```

#### Compliant Code:

```kotlin
fun isNull(str: String) = str == null
```

### ExpressionBodySyntax

Functions which only contain a `return` statement can be collapsed to an expression body. This shortens and
cleans up the code.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
fun stuff(): Int {
    return 5
}
```

#### Compliant Code:

```kotlin
fun stuff() = 5
```

### ForbiddenComment

This rule allows to set a list of comments which are forbidden in the codebase and should only be used during
development. Offending code comments will then be reported.

**Severity**: Style

**Debt**: 10min

#### Configuration options:

* `values` (default: `'TODO:,FIXME:,STOPSHIP:'`)

   forbidden comment strings

#### Noncompliant Code:

```kotlin
// TODO:,FIXME:,STOPSHIP:
fun foo() { }
```

### ForbiddenImport

This rule allows to set a list of forbidden imports. This can be used to discourage the use of unstable, experimental
or deprecated APIs. Detekt will then report all imports that are forbidden.

**Severity**: Style

**Debt**: 10min

#### Configuration options:

* `imports` (default: `''`)

   imports which should not be used

#### Noncompliant Code:

```kotlin
package foo

import kotlin.jvm.JvmField
import kotlin.SinceKotlin
```

### FunctionOnlyReturningConstant

A function that only returns a single constant can be misleading. Instead prefer to define the constant directly
as a `const val`.

**Severity**: Style

**Debt**: 10min

#### Configuration options:

* `ignoreOverridableFunction` (default: `true`)

   if overriden functions should be ignored

* `excludedFunctions` (default: `'describeContents'`)

   excluded functions

#### Noncompliant Code:

```kotlin
fun functionReturningConstantString() = "1"
```

#### Compliant Code:

```kotlin
const val constantString = "1"
```

### LoopWithTooManyJumpStatements

Loops which contain multiple `break` or `continue` statements are hard to read and understand.
To increase readability they should be refactored into simpler loops.

**Severity**: Style

**Debt**: 10min

#### Configuration options:

* `maxJumpCount` (default: `1`)

   maximum allowed jumps in a loop

#### Noncompliant Code:

```kotlin
val strs = listOf("foo, bar")
for (str in strs) {
    if (str == "bar") {
        break
    } else {
        continue
    }
}
```

### MagicNumber

This rule detects and reports usages of magic numbers in the code. Prefer defining constants with clear names
describing what the magic number means.

**Severity**: Style

**Debt**: 10min

#### Configuration options:

* `ignoreNumbers` (default: `'-1,0,1,2'`)

   numbers which do not count as magic numbers

* `ignoreHashCodeFunction` (default: `false`)

   whether magic numbers in hashCode functions should be ignored

* `ignorePropertyDeclaration` (default: `false`)

   whether magic numbers in property declarations should be ignored

* `ignoreConstantDeclaration` (default: `true`)

   whether magic numbers in property declarations should be ignored

* `ignoreCompanionObjectPropertyDeclaration` (default: `true`)

   whether magic numbers in companion object
declarations should be ignored

* `ignoreAnnotation` (default: `false`)

   whether magic numbers in annotations should be ignored

* `ignoreNamedArgument` (default: `true`)

   whether magic numbers in named arguments should be ignored

* `ignoreEnums` (default: `false`)

   whether magic numbers in enums should be ignored

#### Noncompliant Code:

```kotlin
class User {

    fun checkName(name: String) {
        if (name.length > 42) {
            throw IllegalArgumentException("username is too long")
        }
        // ...
    }
}
```

#### Compliant Code:

```kotlin
class User {

    fun checkName(name: String) {
        if (name.length > MAX_USERNAME_SIZE) {
            throw IllegalArgumentException("username is too long")
        }
        // ...
    }

    companion object {
        private const val MAX_USERNAME_SIZE = 42
    }
}
```

### MaxLineLength

This rule reports lines of code which exceed a defined maximum line length.

Long lines might be hard to read on smaller screens or printouts. Additionally having a maximum line length
in the codebase will help make the code more uniform.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* `maxLineLength` (default: `120`)

   maximum line length

* `excludePackageStatements` (default: `false`)

   if package statements should be ignored

* `excludeImportStatements` (default: `false`)

   if import statements should be ignored

### MayBeConst

This rule identifies and reports properties (`val`) that may be `const val` instead.
Using `const val` can lead to better performance of the resulting bytecode as well as better interoperability with
Java.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val myConstant = "abc"
```

#### Compliant Code:

```kotlin
const val MY_CONSTANT = "abc"
```

### ModifierOrder

This rule reports cases in the code where modifiers are not in the correct order. The default modifier order is
taken from: http://kotlinlang.org/docs/reference/coding-conventions.html#modifiers

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
lateinit internal private val str: String
```

#### Compliant Code:

```kotlin
private internal lateinit val str: String
```

### NestedClassesVisibility

Nested classes are often used to implement functionality local to the class it is nested in. Therefore it should
not be public to other parts of the code.
Prefer keeping nested classes `private`.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
internal class NestedClassesVisibility {

    public class NestedPublicClass // should not be public
}
```

#### Compliant Code:

```kotlin
internal class NestedClassesVisibility {

    internal class NestedPublicClass
}
```

### NewLineAtEndOfFile

This rule reports files which do not end with a line separator.

**Severity**: Style

**Debt**: 5min

### NoTabs

This rule reports if tabs are used in Kotlin files.
According to
[Google's Kotlin style guide](https://android.github.io/kotlin-guides/style.html#whitespace-characters)
the only whitespace chars that are allowed in a source file are the line terminator sequence
and the ASCII horizontal space character (0x20).

**Severity**: Style

**Debt**: 5min

### OptionalAbstractKeyword

This rule reports `abstract` modifiers which are unnecessary and can be removed.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
abstract interface Foo { // abstract keyword not needed

    abstract fun x() // abstract keyword not needed
    abstract var y: Int // abstract keyword not needed
}
```

#### Compliant Code:

```kotlin
interface Foo {

    fun x()
    var y: Int
}
```

### OptionalUnit

It is not necessary to define a return type of `Unit` on functions. This rule detects and reports instances where
the `Unit` return type is specified on functions.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
fun foo(): Unit { }
```

#### Compliant Code:

```kotlin
fun foo() { }
```

### OptionalWhenBraces

This rule reports unnecessary braces in when expressions. These optional braces should be removed.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val i = 1
when (1) {
    1 -> { println("one") } // unnecessary curly braces since there is only one statement
    else -> println("else")
}
```

#### Compliant Code:

```kotlin
val i = 1
when (1) {
    1 -> println("one")
    else -> println("else")
}
```

### ProtectedMemberInFinalClass

Kotlin classes are `final` by default. Thus classes which are not marked as `open` should not contain any `protected`
members. Consider using `private` or `internal` modifiers instead.

**Severity**: Warning

**Debt**: 5min

#### Noncompliant Code:

```kotlin
class ProtectedMemberInFinalClass {
    protected var i = 0
}
```

#### Compliant Code:

```kotlin
class ProtectedMemberInFinalClass {
    private var i = 0
}
```

### RedundantVisibilityModifierRule

This rule checks for redundant visibility modifiers.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
public interface Foo { // public per default

    public fun bar() // public per default
}
```

#### Compliant Code:

```kotlin
interface Foo {

    fun bar()
}
```

### ReturnCount

Restrict the number of return methods allowed in methods.

Having many exit points in a function can be confusing and impacts readability of the
code.

**Severity**: Style

**Debt**: 10min

#### Configuration options:

* `max` (default: `2`)

   define the maximum number of return statements allowed per function

* `excludedFunctions` (default: `"equals"`)

   define functions to be ignored by this check

#### Noncompliant Code:

```kotlin
fun foo(i: Int): String {
    when (i) {
        1 -> return "one"
        2 -> return "two"
        else -> return "other"
    }
}
```

#### Compliant Code:

```kotlin
fun foo(i: Int): String {
    return when (i) {
        1 -> "one"
        2 -> "two"
        else -> "other"
    }
}
```

### SafeCast

This rule inspects casts and reports casts which could be replaced with safe casts instead.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
fun numberMagic(number: Number) {
    val i = if (number is Int) number else null
    // ...
}
```

#### Compliant Code:

```kotlin
fun numberMagic(number: Number) {
    val i = number as? Int
    // ...
}
```

### SerialVersionUIDInSerializableClass

Classes which implement the `Serializable` interface should also correctly declare a `serialVersionUID`.
This rule verifies that a `serialVersionUID` was correctly defined.

**Severity**: Warning

**Debt**: 5min

#### Noncompliant Code:

```kotlin
class IncorrectSerializable : Serializable {

    companion object {
        val serialVersionUID = 1 // wrong declaration for UID
    }
}
```

#### Compliant Code:

```kotlin
class CorrectSerializable : Serializable {

    companion object {
        const val serialVersionUID = 1L
    }
}
```

### SpacingBetweenPackageAndImports

This rule verifies spacing between package and import statements as well as between import statements and class
declarations.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
package foo
import a.b
class Bar { }
```

#### Compliant Code:

```kotlin
package foo

import a.b

class Bar { }
```

### ThrowsCount

Functions should have clear `throw` statements. Functions with many `throw` statements can be harder to read and lead
to confusion. Instead prefer to limit the amount of `throw` statements in a function.

**Severity**: Style

**Debt**: 10min

#### Configuration options:

* `max` (default: `2`)

   maximum amount of throw statements in a method

#### Noncompliant Code:

```kotlin
fun foo(i: Int) {
    when (i) {
        1 -> throw IllegalArgumentException()
        2 -> throw IllegalArgumentException()
        3 -> throw IllegalArgumentException()
    }
}
```

#### Compliant Code:

```kotlin
fun foo(i: Int) {
    when (i) {
        1,2,3 -> throw IllegalArgumentException()
    }
}
```

### TrailingWhitespace

This rule reports lines that end with a whitespace.

**Severity**: Style

**Debt**: 5min

### UnnecessaryAbstractClass

This rule inspects `abstract` classes. In case an `abstract class` does not have any concrete members it should be
refactored into an interfacse. Abstract classes which do not define any `abstract` members should instead be
refactored into concrete classes.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
abstract class OnlyAbstractMembersInAbstractClass { // violation: no concrete members

    abstract val i: Int
    abstract fun f()
}

abstract class OnlyConcreteMembersInAbstractClass { // violation: no abstract members

    val i: Int = 0
    fun f() { }
}
```

### UnnecessaryInheritance

This rule reports unnecessary super types. Inheriting from `Any` or `Object` is unnecessary and should simply be
removed.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
class A : Any()
class B : Object()
```

### UnnecessaryParentheses

This rule reports unnecessary parentheses around expressions.
These unnecessary parentheses can safely be removed.

Added in v1.0.0.RC4

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val local = (5 + 3)

if ((local == 8)) { }

fun foo() {
    function({ input -> println(input) })
}
```

#### Compliant Code:

```kotlin
val local = 5 + 3

if (local == 8) { }

fun foo() {
    function { input -> println(input) }
}
```

### UntilInsteadOfRangeTo

Reports calls to '..' operator instead of calls to 'until'.
'until' is applicable in cases where the upper range value is described as
some value subtracted by 1. 'until' helps to prevent off-by-one errors.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
for (i in 0 .. 10 - 1) {}
val range = 0 .. 10 - 1
```

#### Compliant Code:

```kotlin
for (i in 0 until 10) {}
val range = 0 until 10
```

### UnusedImports

This rule reports unused imports. Unused imports are dead code and should be removed.

**Severity**: Style

**Debt**: 5min

### UnusedPrivateMember

Reports unused private properties, function parameters and functions.
If private properties are unused they should be removed. Otherwise this dead code
can lead to confusion and potential bugs.

**Severity**: Maintainability

**Debt**: 5min

### UseDataClass

Classes that simply hold data should be refactored into a `data class`. Data classes are specialized to hold data
and generate `hashCode`, `equals` and `toString` implementations as well.

Read more about `data class`: https://kotlinlang.org/docs/reference/data-classes.html

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* `excludeAnnotatedClasses` (default: `""`)

   allows to provide a list of annotations that disable

#### Noncompliant Code:

```kotlin
class DataClassCandidate(val i: Int) {

    val i2: Int = 0
}
```

#### Compliant Code:

```kotlin
data class DataClass(val i: Int, val i2: Int)
```

### UtilityClassWithPublicConstructor

A class which only contains utility functions and no concrete implementation can be refactored into an `object`.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
class UtilityClass {

    // public constructor here
    constructor() {
        // ...
    }

    companion object {
        val i = 0
    }
}
```

#### Compliant Code:

```kotlin
class UtilityClass {

    private constructor() {
        // ...
    }

    companion object {
        val i = 0
    }
}
```

### WildcardImport

Wildcard imports should be replaced with imports using fully qualified class names. This helps increase clarity of
which classes are imported and helps prevent naming conflicts.

Library updates can introduce naming clashes with your own classes which might result in compilation errors.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* `excludeImports` (default: `'java.util.*,kotlinx.android.synthetic.*'`)

   Define a whitelist of package names that should be allowed to be imported
with wildcard imports.

#### Noncompliant Code:

```kotlin
package test

import io.gitlab.arturbosch.detekt.*

class DetektElements {
    val element1 = DetektElement1()
    val element2 = DetektElement2()
}
```

#### Compliant Code:

```kotlin
package test

import io.gitlab.arturbosch.detekt.DetektElement1
import io.gitlab.arturbosch.detekt.DetektElement2

class DetektElements {
    val element1 = DetektElement1()
    val element2 = DetektElement2()
}
```
