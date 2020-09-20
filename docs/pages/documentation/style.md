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

### ClassOrdering

This rule ensures class contents are ordered as follows as recommended by the Kotlin
[Coding Conventions](https://kotlinlang.org/docs/reference/coding-conventions.html#class-layout):
- Property declarations and initializer blocks
- Secondary constructors
- Method declarations
- Companion object

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
class OutOfOrder {
    companion object {
        const val IMPORTANT_VALUE = 3
    }

    fun returnX(): Int {
        return x
    }

    private val x = 2
}
```

#### Compliant Code:

```kotlin
class InOrder {
    private val x = 2

    fun returnX(): Int {
        return x
    }

    companion object {
        const val IMPORTANT_VALUE = 3
    }
}
```

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

This rule reports functions inside data classes which have not been marked as a conversion function.

Data classes should mainly be used to store data. This rule assumes that they should not contain any extra functions
aside functions that help with converting objects from/to one another.
Data classes will automatically have a generated `equals`, `toString` and `hashCode` function by the compiler.

**Severity**: Style

**Debt**: 20min

#### Configuration options:

* ``conversionFunctionPrefix`` (default: ``'to'``)

   allowed conversion function names

#### Noncompliant Code:

```kotlin
data class DataClassWithFunctions(val i: Int) {
    fun foo() { }
}
```

### DataClassShouldBeImmutable

This rule reports mutable properties inside data classes.

Data classes should mainly be used to store immutable data. This rule assumes that they should not contain any
mutable properties.

**Severity**: Style

**Debt**: 20min

#### Noncompliant Code:

```kotlin
data class MutableDataClass(var i: Int) {
    var s: String? = null
}
```

#### Compliant Code:

```kotlin
data class ImmutableDataClass(
    val i: Int,
    val s: String?
)
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

### EqualsOnSignatureLine

Requires that the equals sign, when used for an expression style function, is on the same line as the
rest of the function signature.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
fun stuff(): Int
    = 5

fun <V> foo(): Int where V : Int
    = 5
```

#### Compliant Code:

```kotlin
fun stuff() = 5

fun stuff() =
    foo.bar()

fun <V> foo(): Int where V : Int = 5
```

### ExplicitCollectionElementAccessMethod

In Kotlin functions `get` or `set` can be replaced with the shorter operator — `[]`,
see https://kotlinlang.org/docs/reference/operator-overloading.html#indexed.
Prefer the usage of the indexed access operator `[]` for map or list element access or insert methods.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val map = Map<String, String>()
map.put("key", "value")
val value = map.get("key")
```

#### Compliant Code:

```kotlin
val map = Map<String, String>()
map["key"] = "value"
val value = map["key"]
```

### ExplicitItLambdaParameter

Lambda expressions are one of the core features of the language. They often include very small chunks of
code using only one parameter. In this cases Kotlin can supply the implicit `it` parameter
to make code more concise. It fits most usecases, but when faced larger or nested chunks of code,
you might want to add an explicit name for the parameter. Naming it just `it` is meaningless and only
makes your code misleading, especially when dealing with nested functions.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
a?.let { it -> it.plus(1) }
foo.flatMapObservable { it -> Observable.fromIterable(it) }
listOfPairs.map(::second).forEach { it ->
    it.execute()
}
collection.zipWithNext { it, next -> Pair(it, next) }
```

#### Compliant Code:

```kotlin
a?.let { it.plus(1) } // Much better to use implicit it
foo.flatMapObservable(Observable::fromIterable) // Here we can have a method reference

// For multiline blocks it is usually better come up with a clear and more meaningful name
listOfPairs.map(::second).forEach { apiRequest ->
    apiRequest.execute()
}

// Lambdas with multiple parameter should be named clearly, using it for one of them can be confusing
collection.zipWithNext { prev, next ->
    Pair(prev, next)
}
```

### ExpressionBodySyntax

Functions which only contain a `return` statement can be collapsed to an expression body. This shortens and
cleans up the code.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``includeLineWrapping`` (default: ``false``)

   include return statements with line wraps in it

#### Noncompliant Code:

```kotlin
fun stuff(): Int {
    return 5
}
```

#### Compliant Code:

```kotlin
fun stuff() = 5

fun stuff() {
    return
        moreStuff()
            .getStuff()
            .stuffStuff()
}
```

### ForbiddenComment

This rule allows to set a list of comments which are forbidden in the codebase and should only be used during
development. Offending code comments will then be reported.

**Severity**: Style

**Debt**: 10min

#### Configuration options:

* ``values`` (default: ``['TODO:', 'FIXME:', 'STOPSHIP:']``)

   forbidden comment strings

* ``allowedPatterns`` (default: ``''``)

   ignores comments which match the specified regular expression.
For example `Ticket|Task`.

#### Noncompliant Code:

```kotlin
val a = "" // TODO: remove please
// FIXME: this is a hack
fun foo() { }
// STOPSHIP:
```

### ForbiddenImport

This rule allows to set a list of forbidden imports. This can be used to discourage the use of unstable, experimental
or deprecated APIs. Detekt will then report all imports that are forbidden.

**Severity**: Style

**Debt**: 10min

#### Configuration options:

* ``imports`` (default: ``[]``)

   imports which should not be used

* ``forbiddenPatterns`` (default: ``''``)

   reports imports which match the specified regular expression. For example `net.*R`.

#### Noncompliant Code:

```kotlin
package foo

import kotlin.jvm.JvmField
import kotlin.SinceKotlin
```

### ForbiddenMethodCall

This rule allows to set a list of forbidden methods. This can be used to discourage the use of unstable, experimental
or deprecated methods, especially for methods imported from external libraries.
Detekt will then report all methods invocation that are forbidden.

**Requires Type Resolution**

**Severity**: Style

**Debt**: 10min

#### Configuration options:

* ``methods`` (default: ``['kotlin.io.println', 'kotlin.io.print']``)

   Comma separated list of fully qualified method signatures which are forbidden

#### Noncompliant Code:

```kotlin
import java.lang.System
fun main() {
    System.gc()
}
```

### ForbiddenPublicDataClass

The data classes are bad for the binary compatibility in public APIs. Avoid to use it.

This rule is aimed to library maintainers. If you are developing a final application you don't need to care about
this issue.

More info: https://jakewharton.com/public-api-challenges-in-kotlin/

**Severity**: Style

**Debt**: 20min

#### Configuration options:

* ``ignorePackages`` (default: ``['*.internal', '*.internal.*']``)

   ignores classes in the specified packages.

#### Noncompliant Code:

```kotlin
data class C(val a: String) // violation: public data class
```

#### Compliant Code:

```kotlin
internal data class C(val a: String)
```

### ForbiddenVoid

This rule detects usages of `Void` and reports them as forbidden.
The Kotlin type `Unit` should be used instead. This type corresponds to the `Void` class in Java
and has only one value - the `Unit` object.

**Requires Type Resolution**

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``ignoreOverridden`` (default: ``false``)

   ignores void types in signatures of overridden functions

* ``ignoreUsageInGenerics`` (default: ``false``)

   ignore void types as generic arguments

#### Noncompliant Code:

```kotlin
runnable: () -> Void
var aVoid: Void? = null
```

#### Compliant Code:

```kotlin
runnable: () -> Unit
Void::class
```

### FunctionOnlyReturningConstant

A function that only returns a single constant can be misleading. Instead prefer to define the constant directly
as a `const val`.

**Severity**: Style

**Debt**: 10min

#### Configuration options:

* ``ignoreOverridableFunction`` (default: ``true``)

   if overriden functions should be ignored

* ``excludedFunctions`` (default: ``'describeContents'``)

   excluded functions

* ``excludeAnnotatedFunction`` (default: ``['dagger.Provides']``)

   allows to provide a list of annotations that disable this check

#### Noncompliant Code:

```kotlin
fun functionReturningConstantString() = "1"
```

#### Compliant Code:

```kotlin
const val constantString = "1"
```

### LibraryCodeMustSpecifyReturnType

Library functions/properties should have an explicit return type.
Inferred return type can easily be changed by mistake which may lead to breaking changes.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
// code from a library
val strs = listOf("foo, bar")
fun bar() = 5
class Parser {
    fun parse() = ...
}
```

#### Compliant Code:

```kotlin
// code from a library
val strs: List<String> = listOf("foo, bar")
fun bar(): Int = 5

class Parser {
    fun parse(): ParsingResult = ...
}
```

### LibraryEntitiesShouldNotBePublic

Library typealias and classes should be internal or private.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
// code from a library
class A
```

#### Compliant Code:

```kotlin
// code from a library
internal class A
```

### LoopWithTooManyJumpStatements

Loops which contain multiple `break` or `continue` statements are hard to read and understand.
To increase readability they should be refactored into simpler loops.

**Severity**: Style

**Debt**: 10min

#### Configuration options:

* ``maxJumpCount`` (default: ``1``)

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

* ``ignoreNumbers`` (default: ``['-1', '0', '1', '2']``)

   numbers which do not count as magic numbers

* ``ignoreHashCodeFunction`` (default: ``true``)

   whether magic numbers in hashCode functions should be ignored

* ``ignorePropertyDeclaration`` (default: ``false``)

   whether magic numbers in property declarations should be ignored

* ``ignoreLocalVariableDeclaration`` (default: ``false``)

   whether magic numbers in local variable declarations should be
ignored

* ``ignoreConstantDeclaration`` (default: ``true``)

   whether magic numbers in constant declarations should be ignored

* ``ignoreCompanionObjectPropertyDeclaration`` (default: ``true``)

   whether magic numbers in companion object
declarations should be ignored

* ``ignoreAnnotation`` (default: ``false``)

   whether magic numbers in annotations should be ignored

* ``ignoreNamedArgument`` (default: ``true``)

   whether magic numbers in named arguments should be ignored

* ``ignoreEnums`` (default: ``false``)

   whether magic numbers in enums should be ignored

* ``ignoreRanges`` (default: ``false``)

   whether magic numbers in ranges should be ignored

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

### MandatoryBracesIfStatements

This rule detects multi-line `if` statements which do not have braces.
Adding braces would improve readability and avoid possible errors.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val i = 1
if (i > 0)
    println(i)
```

#### Compliant Code:

```kotlin
val x = if (condition) 5 else 4
```

### MandatoryBracesLoops

This rule detects multi-line `for` and `while` loops which do not have braces.
Adding braces would improve readability and avoid possible errors.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
for (i in 0..10)
    println(i)

while (true)
    println("Hello, world")

do
    println("Hello, world")
while (true)
```

#### Compliant Code:

```kotlin
for (i in 0..10) {
    println(i)
}

for (i in 0..10) println(i)

while (true) {
    println("Hello, world")
}

while (true) println("Hello, world")

do {
    println("Hello, world")
} while (true)

do println("Hello, world") while (true)
```

### MaxLineLength

This rule reports lines of code which exceed a defined maximum line length.

Long lines might be hard to read on smaller screens or printouts. Additionally having a maximum line length
in the codebase will help make the code more uniform.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``maxLineLength`` (default: ``120``)

   maximum line length

* ``excludePackageStatements`` (default: ``true``)

   if package statements should be ignored

* ``excludeImportStatements`` (default: ``true``)

   if import statements should be ignored

* ``excludeCommentStatements`` (default: ``false``)

   if comment statements should be ignored

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

Nested classes inherit their visibility from the parent class
and are often used to implement functionality local to the class it is nested in.
These nested classes can't have a higher visibility than their parent.
However, the visibility can be further restricted by using a private modifier for instance.
In internal classes the _explicit_ public modifier for nested classes is misleading and thus unnecessary,
because the nested class still has an internal visibility.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
internal class Outer {
    // explicit public modifier still results in an internal nested class
    public class Nested
}
```

#### Compliant Code:

```kotlin
internal class Outer {
    class Nested1
    internal class Nested2
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
and the ASCII horizontal space character (0x20). Strings containing tabs are allowed.

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

It is not necessary to define a return type of `Unit` on functions or to specify a lone Unit statement.
This rule detects and reports instances where the `Unit` return type is specified on functions and the occurrences
of a lone Unit statement.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
fun foo(): Unit {
    return Unit 
}
fun foo() = Unit

fun doesNothing() {
    Unit
}
```

#### Compliant Code:

```kotlin
fun foo() { }

// overridden no-op functions are allowed
override fun foo() = Unit
```

### OptionalWhenBraces

This rule reports unnecessary braces in when expressions. These optional braces should be removed.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val i = 1
when (i) {
    1 -> { println("one") } // unnecessary curly braces since there is only one statement
    else -> println("else")
}
```

#### Compliant Code:

```kotlin
val i = 1
when (i) {
    1 -> println("one")
    else -> println("else")
}
```

### PreferToOverPairSyntax

This rule detects the usage of the Pair constructor to create pairs of values.

Using <value1> to <value2> is preferred.

**Requires Type Resolution**

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val pair = Pair(1, 2)
```

#### Compliant Code:

```kotlin
val pair = 1 to 2
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

### RedundantExplicitType

Local properties do not need their type to be explicitly provided when the inferred type matches the explicit type.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
fun function() {
val x: String = "string"
}
```

#### Compliant Code:

```kotlin
fun function() {
val x = "string"
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

* ``max`` (default: ``2``)

   define the maximum number of return statements allowed per function

* ``excludedFunctions`` (default: ``'equals'``)

   define functions to be ignored by this check

* ``excludeLabeled`` (default: ``false``)

   if labeled return statements should be ignored

* ``excludeReturnFromLambda`` (default: ``true``)

   if labeled return from a lambda should be ignored

* ``excludeGuardClauses`` (default: ``false``)

   if true guard clauses at the beginning of a method should be ignored

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

* ``max`` (default: ``2``)

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

### UnderscoresInNumericLiterals

This rule detects and reports decimal base 10 numeric literals above a certain length that should be underscore
separated for readability. Underscores that do not make groups of 3 digits are also reported even if their length is
under the `acceptableDecimalLength`. For `Serializable` classes or objects, the field `serialVersionUID` is
explicitly ignored. For floats and doubles, anything to the right of the decimal is ignored.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``acceptableDecimalLength`` (default: ``5``)

   Length under which decimal base 10 literals are not required to have
underscores

#### Noncompliant Code:

```kotlin
object Money {
    const val DEFAULT_AMOUNT = 1000000
}
```

#### Compliant Code:

```kotlin
object Money {
    const val DEFAULT_AMOUNT = 1_000_000
}
```

### UnnecessaryAbstractClass

This rule inspects `abstract` classes. In case an `abstract class` does not have any concrete members it should be
refactored into an interface. Abstract classes which do not define any `abstract` members should instead be
refactored into concrete classes.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``excludeAnnotatedClasses`` (default: ``['dagger.Module']``)

   Allows you to provide a list of annotations that disable
this check.

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

### UnnecessaryAnnotationUseSiteTarget

This rule inspects the use of the Annotation use-site Target. In case that the use-site Target is not needed it can
be removed. For more information check the kotlin documentation:
https://kotlinlang.org/docs/reference/annotations.html#annotation-use-site-targets

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
@property:Inject private val foo: String = "bar" // violation: unnecessary @property:

class Module(@param:Inject private val foo: String) // violation: unnecessary @param:
```

#### Compliant Code:

```kotlin
class Module(@Inject private val foo: String)
```

### UnnecessaryApply

`apply` expressions are used frequently, but sometimes their usage should be replaced with
an ordinary method/extension function call to reduce visual complexity

**Requires Type Resolution**

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
config.apply { version = "1.2" } // can be replaced with `config.version = "1.2"`
config?.apply { environment = "test" } // can be replaced with `config?.environment = "test"`
config?.apply { println(version) } // `apply` can be replaced by `let`
```

#### Compliant Code:

```kotlin
config.apply {
    version = "1.2"
    environment = "test"
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

### UnnecessaryLet

`let` expressions are used extensively in our code for null-checking and chaining functions,
but sometimes their usage should be replaced with a ordinary method/extension function call
to reduce visual complexity

**Requires Type Resolution**

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
a.let { print(it) } // can be replaced with `print(a)`
a.let { it.plus(1) } // can be replaced with `a.plus(1)`
a?.let { it.plus(1) } // can be replaced with `a?.plus(1)`
a?.let { that -> that.plus(1) }?.let { it.plus(1) } // can be replaced with `a?.plus(1)?.plus(1)`
a.let { 1.plus(1) } // can be replaced with `1.plus(1)`
a?.let { 1.plus(1) } // can be replaced with `if (a != null) 1.plus(1)`
```

#### Compliant Code:

```kotlin
a?.let { print(it) }
a?.let { 1.plus(it) } ?.let { msg -> print(msg) }
a?.let { it.plus(it) }
val b = a?.let { 1.plus(1) }
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
Exempt from this rule are imports resulting from references to elements within KDoc and
from destructuring declarations (componentN imports).

**Severity**: Style

**Debt**: 5min

### UnusedPrivateClass

Reports unused private classes.
If private classes are unused they should be removed. Otherwise this dead code
can lead to confusion and potential bugs.

**Severity**: Maintainability

**Debt**: 5min

**Aliases**: unused

### UnusedPrivateMember

Reports unused private properties, function parameters and functions.
If these private elements are unused they should be removed. Otherwise this dead code
can lead to confusion and potential bugs.

**Severity**: Maintainability

**Debt**: 5min

**Aliases**: UNUSED_VARIABLE, UNUSED_PARAMETER, unused

#### Configuration options:

* ``allowedNames`` (default: ``'(_|ignored|expected|serialVersionUID)'``)

   unused private member names matching this regex are ignored

### UseArrayLiteralsInAnnotations

This rule detects annotations which use the arrayOf(...) syntax instead of the array literal [...] syntax.
The latter should be preferred as it is more readable.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
@PositiveCase(arrayOf("..."))
```

#### Compliant Code:

```kotlin
@NegativeCase(["..."])
```

### UseCheckNotNull

Turn on this rule to flag `check` calls for not-null check that can be replaced with a `checkNotNull` call.

**Requires Type Resolution**

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
check(x != null)
```

#### Compliant Code:

```kotlin
checkNotNull(x)
```

### UseCheckOrError

Kotlin provides a much more concise way to check invariants as well as pre- and post conditions.
Prefer them instead of manually throwing an IllegalStateException.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
if (value == null) throw IllegalStateException("value should not be null")
if (value < 0) throw IllegalStateException("value is $value but should be at least 0")
when(a) {
1 -> doSomething()
else -> throw IllegalStateException("Unexpected value")
}
```

#### Compliant Code:

```kotlin
checkNotNull(value) { "value should not be null" }
check(value >= 0) { "value is $value but should be at least 0" }
when(a) {
1 -> doSomething()
else -> error("Unexpected value")
}
```

### UseDataClass

Classes that simply hold data should be refactored into a `data class`. Data classes are specialized to hold data
and generate `hashCode`, `equals` and `toString` implementations as well.

Read more about `data class`: https://kotlinlang.org/docs/reference/data-classes.html

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``excludeAnnotatedClasses`` (default: ``[]``)

   allows to provide a list of annotations that disable this check

* ``allowVars`` (default: ``false``)

   allows to relax this rule in order to exclude classes that contains one (or more) Vars

#### Noncompliant Code:

```kotlin
class DataClassCandidate(val i: Int) {
    val i2: Int = 0
}
```

#### Compliant Code:

```kotlin
data class DataClass(val i: Int, val i2: Int)

// classes with delegating interfaces are compliant
interface I
class B() : I
class A(val b: B) : I by b
```

### UseEmptyCounterpart

Instantiation of an object's "empty" state should use the object's "empty" initializer for clarity purposes.

**Requires Type Resolution**

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
arrayOf()
listOf() // or listOfNotNull()
mapOf()
sequenceOf()
setOf()
```

#### Compliant Code:

```kotlin
emptyArray()
emptyList()
emptyMap()
emptySequence()
emptySet()
```

### UseIfInsteadOfWhen

Binary expressions are better expressed using an `if` expression than a `when` expression.

See https://kotlinlang.org/docs/reference/coding-conventions.html#if-versus-when

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
when (x) {
null -> true
else -> false
}
```

#### Compliant Code:

```kotlin
if (x == null) true else false
```

### UseRequire

Kotlin provides a much more concise way to check preconditions than to manually throw an
IllegalArgumentException.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
if (value == null) throw IllegalArgumentException("value should not be null")
if (value < 0) throw IllegalArgumentException("value is $value but should be at least 0")
```

#### Compliant Code:

```kotlin
requireNotNull(value) { "value should not be null" }
require(value >= 0) { "value is $value but should be at least 0" }
```

### UseRequireNotNull

Turn on this rule to flag `require` calls for not-null check that can be replaced with a `requireNotNull` call.

**Requires Type Resolution**

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
require(x != null)
```

#### Compliant Code:

```kotlin
requireNotNull(x)
```

### UselessCallOnNotNull

The Kotlin stdlib provides some functions that are designed to operate on references that may be null. These
functions can also be called on non-nullable references or on collections or sequences that are known to be empty -
the calls are redundant in this case and can be removed or should be changed to a call that does not check whether
the value is null or not.

Rule adapted from Kotlin's IntelliJ plugin: https://github.com/JetBrains/kotlin/blob/f5d0a38629e7d2e7017ee645dc4d4bee60614e93/idea/src/org/jetbrains/kotlin/idea/inspections/collections/UselessCallOnNotNullInspection.kt

**Requires Type Resolution**

**Severity**: Performance

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val testList = listOf("string").orEmpty()
val testList2 = listOf("string").orEmpty().map { _ }
val testList3 = listOfNotNull("string")
val testString = ""?.isNullOrBlank()
```

#### Compliant Code:

```kotlin
val testList = listOf("string")
val testList2 = listOf("string").map { }
val testList3 = listOf("string")
val testString = ""?.isBlank()
```

### UtilityClassWithPublicConstructor

A class which only contains utility variables and functions with no concrete implementation can be refactored
into an `object` or an class with a non-public constructor.
Furthermore, this rule reports utility classes which are not final.

**Severity**: Style

**Debt**: 5min

#### Noncompliant Code:

```kotlin
class UtilityClassViolation {

    // public constructor here
    constructor() {
        // ...
    }

    companion object {
        val i = 0
    }
}

open class UtilityClassViolation private constructor() {

    // ...
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
object UtilityClass {

    val i = 0
}
```

### VarCouldBeVal

Reports var declarations (locally-scoped variables) that could be val, as they are not re-assigned.
Val declarations are assign-once (read-only), which makes understanding the current state easier.

**Severity**: Maintainability

**Debt**: 5min

**Aliases**: CanBeVal

#### Noncompliant Code:

```kotlin
fun example() {
    var i = 1 // violation: this variable is never re-assigned
    val j = i + 1
}
```

#### Compliant Code:

```kotlin
fun example() {
    val i = 1
    val j = i + 1
}
```

### WildcardImport

Wildcard imports should be replaced with imports using fully qualified class names. This helps increase clarity of
which classes are imported and helps prevent naming conflicts.

Library updates can introduce naming clashes with your own classes which might result in compilation errors.

**NOTE**: This rule has a twin implementation NoWildcardImports in the formatting rule set (a wrapped KtLint rule).
When suppressing an issue of WildcardImport in the baseline file, make sure to suppress the corresponding NoWildcardImports issue.

**Severity**: Style

**Debt**: 5min

#### Configuration options:

* ``excludeImports`` (default: ``['java.util.*', 'kotlinx.android.synthetic.*']``)

   Define a list of package names that should be allowed to be imported
with wildcard imports.

#### Noncompliant Code:

```kotlin
import io.gitlab.arturbosch.detekt.*

class DetektElements {
    val element1 = DetektElement1()
    val element2 = DetektElement2()
}
```

#### Compliant Code:

```kotlin
import io.gitlab.arturbosch.detekt.DetektElement1
import io.gitlab.arturbosch.detekt.DetektElement2

class DetektElements {
    val element1 = DetektElement1()
    val element2 = DetektElement2()
}
```
