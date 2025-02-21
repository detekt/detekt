---
title: Potential-bugs Rule Set
sidebar: home_sidebar
keywords: [rules, potential-bugs]
permalink: potential-bugs.html
toc: true
folder: documentation
---
The potential-bugs rule set provides rules that detect potential bugs.

### AvoidReferentialEquality

Kotlin supports two types of equality: structural equality and referential equality. While there are
use cases for both, checking for referential equality for some types (such as `String` or `List`) is
likely not intentional and may cause unexpected results.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

**Debt**: 5min

#### Configuration options:

* ``forbiddenTypePatterns`` (default: ``['kotlin.String']``)

  Specifies those types for which referential equality checks are considered a rule violation. The types are defined by a list of simple glob patterns (supporting `*` and `?` wildcards) that match the fully qualified type name.

#### Noncompliant Code:

```kotlin
    val areEqual = "aString" === otherString
    val areNotEqual = "aString" !== otherString
```

#### Compliant Code:

```kotlin
    val areEqual = "aString" == otherString
    val areNotEqual = "aString" != otherString
```

### CastNullableToNonNullableType

Reports cast of nullable variable to non-null type. Cast like this can hide `null`
problems in your code. The compliant code would be that which will correctly check
for two things (nullability and type) and not just one (cast).

**Active by default**: No

**Requires Type Resolution**

**Debt**: 5min

#### Noncompliant Code:

```kotlin
fun foo(bar: Any?) {
    val x = bar as String
}
```

#### Compliant Code:

```kotlin
fun foo(bar: Any?) {
    val x = checkNotNull(bar) as String
}

// Alternative
fun foo(bar: Any?) {
    val x = (bar ?: error("null assertion message")) as String
}
```

### CastToNullableType

Reports unsafe cast to nullable types.
`as String?` is unsafed and may be misused as safe cast (`as? String`).

**Active by default**: No

**Debt**: 5min

#### Noncompliant Code:

```kotlin
fun foo(a: Any?) {
    val x: String? = a as String? // If 'a' is not String, ClassCastException will be thrown.
}
```

#### Compliant Code:

```kotlin
fun foo(a: Any?) {
    val x: String? = a as? String
}
```

### Deprecation

Deprecated elements are expected to be removed in the future. Alternatives should be found if possible.

**Active by default**: No

**Requires Type Resolution**

**Debt**: 20min

**Aliases**: DEPRECATION

### DontDowncastCollectionTypes

Down-casting immutable types from kotlin.collections should be discouraged.
The result of the downcast is platform specific and can lead to unexpected crashes.
Prefer to use instead the `toMutable<Type>()` functions.

**Active by default**: No

**Requires Type Resolution**

**Debt**: 10min

#### Noncompliant Code:

```kotlin
val list : List<Int> = getAList()
if (list is MutableList) {
    list.add(42)
}

(list as MutableList).add(42)
```

#### Compliant Code:

```kotlin
val list : List<Int> = getAList()
list.toMutableList().add(42)
```

### DoubleMutabilityForCollection

Using `var` when declaring a mutable collection or value holder leads to double mutability.
Consider instead declaring your variable with `val` or switching your declaration to use an
immutable type.

By default, the rule triggers on standard mutable collections, however it can be configured
to trigger on other types of mutable value types, such as `MutableState` from Jetpack
Compose.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

**Debt**: 5min

**Aliases**: DoubleMutability

#### Configuration options:

* ``mutableTypes`` (default: ``['kotlin.collections.MutableList', 'kotlin.collections.MutableMap', 'kotlin.collections.MutableSet', 'java.util.ArrayList', 'java.util.LinkedHashSet', 'java.util.HashSet', 'java.util.LinkedHashMap', 'java.util.HashMap']``)

  Define a list of mutable types to trigger on when defined with `var`.

#### Noncompliant Code:

```kotlin
var myList = mutableListOf(1,2,3)
var mySet = mutableSetOf(1,2,3)
var myMap = mutableMapOf("answer" to 42)
```

#### Compliant Code:

```kotlin
// Use val
val myList = mutableListOf(1,2,3)
val mySet = mutableSetOf(1,2,3)
val myMap = mutableMapOf("answer" to 42)

// Use immutable types
var myList = listOf(1,2,3)
var mySet = setOf(1,2,3)
var myMap = mapOf("answer" to 42)
```

### ~~DuplicateCaseInWhenExpression~~

Rule deprecated as compiler performs this check by default

Flags duplicate `case` statements in `when` expressions.

If a `when` expression contains the same `case` statement multiple times they should be merged. Otherwise, it might be
easy to miss one of the cases when reading the code, leading to unwanted side effects.

**Active by default**: Yes - Since v1.0.0

**Debt**: 10min

#### Noncompliant Code:

```kotlin
when (i) {
    1 -> println("one")
    1 -> println("one")
    else -> println("else")
}
```

#### Compliant Code:

```kotlin
when (i) {
    1 -> println("one")
    else -> println("else")
}
```

### ElseCaseInsteadOfExhaustiveWhen

This rule reports `when` expressions that contain an `else` case even though they have an exhaustive set of cases.

This occurs when the subject of the `when` expression is either an enum class, sealed class or of type boolean.
Using `else` cases for these expressions can lead to unintended behavior when adding new enum types, sealed subtypes
or changing the nullability of a boolean, since this will be implicitly handled by the `else` case.

**Active by default**: No

**Requires Type Resolution**

**Debt**: 5min

#### Configuration options:

* ``ignoredSubjectTypes`` (default: ``[]``)

  List of fully qualified types which should be ignored for when expressions with a subject. Example `kotlinx.serialization.json.JsonObject`

#### Noncompliant Code:

```kotlin
enum class Color {
    RED,
    GREEN,
    BLUE
}

when(c) {
    Color.RED -> {}
    Color.GREEN -> {}
    else -> {}
}
```

#### Compliant Code:

```kotlin
enum class Color {
    RED,
    GREEN,
    BLUE
}

when(c) {
    Color.RED -> {}
    Color.GREEN -> {}
    Color.BLUE -> {}
}
```

### EqualsAlwaysReturnsTrueOrFalse

Reports `equals()` methods which will always return true or false.

Equals methods should always report if some other object is equal to the current object.
See the Kotlin documentation for Any.equals(other: Any?):
https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html

**Active by default**: Yes - Since v1.2.0

**Debt**: 20min

#### Noncompliant Code:

```kotlin
override fun equals(other: Any?): Boolean {
    return true
}
```

#### Compliant Code:

```kotlin
override fun equals(other: Any?): Boolean {
    return this === other
}
```

### EqualsWithHashCodeExist

When a class overrides the equals() method it should also override the hashCode() method.

All hash-based collections depend on objects meeting the equals-contract. Two equal objects must produce the
same hashcode. When inheriting equals or hashcode, override the inherited and call the super method for
clarification.

**Active by default**: Yes - Since v1.0.0

**Debt**: 5min

#### Noncompliant Code:

```kotlin
class Foo {

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}
```

#### Compliant Code:

```kotlin
class Foo {

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
```

### ExitOutsideMain

Reports the usage of `System.exit()`, `Runtime.exit()`, `Runtime.halt()` and Kotlin's `exitProcess()`
when used outside the `main` function.
This makes code more difficult to test, causes unexpected behaviour on Android, and is a poor way to signal a
failure in the program. In almost all cases it is more appropriate to throw an exception.

**Active by default**: No

**Requires Type Resolution**

**Debt**: 10min

#### Noncompliant Code:

```kotlin
fun randomFunction() {
    val result = doWork()
    if (result == FAILURE) {
        exitProcess(2)
    } else {
        exitProcess(0)
    }
}
```

#### Compliant Code:

```kotlin
fun main() {
    val result = doWork()
    if (result == FAILURE) {
        exitProcess(2)
    } else {
        exitProcess(0)
    }
}
```

### ExplicitGarbageCollectionCall

Reports all calls to explicitly trigger the Garbage Collector.
Code should work independently of the garbage collector and should not require the GC to be triggered in certain
points in time.

**Active by default**: Yes - Since v1.0.0

**Debt**: 20min

#### Noncompliant Code:

```kotlin
System.gc()
Runtime.getRuntime().gc()
System.runFinalization()
```

### HasPlatformType

Platform types must be declared explicitly in public APIs to prevent unexpected errors.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

**Debt**: 5min

#### Noncompliant Code:

```kotlin
class Person {
    fun apiCall() = System.getProperty("propertyName")
}
```

#### Compliant Code:

```kotlin
class Person {
    fun apiCall(): String = System.getProperty("propertyName")
}
```

### IgnoredReturnValue

This rule warns on instances where a function, annotated with either `@CheckReturnValue` or `@CheckResult`,
returns a value but that value is not used in any way. The Kotlin compiler gives no warning for this scenario
normally so that's the rationale behind this rule.

fun returnsValue() = 42
fun returnsNoValue() {}

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

**Debt**: 20min

#### Configuration options:

* ~~``restrictToAnnotatedMethods``~~ (default: ``true``)

  **Deprecated**: Use `restrictToConfig` instead

  if the rule should check only annotated methods

* ``restrictToConfig`` (default: ``true``)

  If the rule should check only methods matching to configuration, or all methods

* ``returnValueAnnotations`` (default: ``['CheckResult', '*.CheckResult', 'CheckReturnValue', '*.CheckReturnValue']``)

  List of glob patterns to be used as inspection annotation

* ``ignoreReturnValueAnnotations`` (default: ``['CanIgnoreReturnValue', '*.CanIgnoreReturnValue']``)

  Annotations to skip this inspection

* ``returnValueTypes`` (default: ``['kotlin.sequences.Sequence', 'kotlinx.coroutines.flow.*Flow', 'java.util.stream.*Stream']``)

  List of return types that should not be ignored

* ``ignoreFunctionCall`` (default: ``[]``)

  List of function signatures which should be ignored by this rule. Specifying fully-qualified function signature with name only (i.e. `java.time.LocalDate.now`) will ignore all function calls matching the name. Specifying fully-qualified function signature with parameters (i.e. `java.time.LocalDate.now(java.time.Clock)`) will ignore only function calls matching the name and parameters exactly.

#### Noncompliant Code:

```kotlin
returnsValue()
```

#### Compliant Code:

```kotlin
if (42 == returnsValue()) {}
val x = returnsValue()
```

### ImplicitDefaultLocale

Prefer passing [java.util.Locale] explicitly than using implicit default value when formatting
strings or performing a case conversion.

The default locale is almost always inappropriate for machine-readable text like HTTP headers.
For example, if locale with tag `ar-SA-u-nu-arab` is a current default then `%d` placeholders
will be evaluated to a number consisting of Eastern-Arabic (non-ASCII) digits.
[java.util.Locale.US] is recommended for machine-readable output.

**Active by default**: Yes - Since v1.16.0

**Debt**: 5min

#### Noncompliant Code:

```kotlin
String.format("Timestamp: %d", System.currentTimeMillis())
```

#### Compliant Code:

```kotlin
String.format(Locale.US, "Timestamp: %d", System.currentTimeMillis())
```

### ImplicitUnitReturnType

Functions using expression statements have an implicit return type.
Changing the type of the expression accidentally, changes the functions return type.
This may lead to backward incompatibility.
Use a block statement to make clear this function will never return a value.

**Active by default**: No

**Requires Type Resolution**

**Debt**: 5min

#### Configuration options:

* ``allowExplicitReturnType`` (default: ``true``)

  if functions with explicit `Unit` return type should be allowed

#### Noncompliant Code:

```kotlin
fun errorProneUnit() = println("Hello Unit")
fun errorProneUnitWithParam(param: String) = param.run { println(this) }
fun String.errorProneUnitWithReceiver() = run { println(this) }
```

#### Compliant Code:

```kotlin
fun blockStatementUnit() {
    // code
}

// explicit Unit is compliant by default; can be configured to enforce block statement
fun safeUnitReturn(): Unit = println("Hello Unit")
```

### InvalidRange

Reports ranges which are empty.
This might be a bug if it is used for instance as a loop condition. This loop will never be triggered then.
This might be due to invalid ranges like (10..9) which will cause the loop to never be entered.

**Active by default**: Yes - Since v1.2.0

**Debt**: 10min

#### Noncompliant Code:

```kotlin
for (i in 2..1) {}
for (i in 1 downTo 2) {}

val range1 = 2 until 1
val range2 = 2 until 2
```

#### Compliant Code:

```kotlin
for (i in 2..2) {}
for (i in 2 downTo 2) {}

val range = 2 until 3
```

### IteratorHasNextCallsNextMethod

Verifies implementations of the Iterator interface.
The hasNext() method of an Iterator implementation should not have any side effects.
This rule reports implementations that call the next() method of the Iterator inside the hasNext() method.

**Active by default**: Yes - Since v1.2.0

**Debt**: 10min

#### Noncompliant Code:

```kotlin
class MyIterator : Iterator<String> {

    override fun hasNext(): Boolean {
        return next() != null
    }
}
```

### IteratorNotThrowingNoSuchElementException

Reports implementations of the `Iterator` interface which do not throw a NoSuchElementException in the
implementation of the next() method. When there are no more elements to return an Iterator should throw a
NoSuchElementException.

See: https://docs.oracle.com/javase/7/docs/api/java/util/Iterator.html#next()

**Active by default**: Yes - Since v1.2.0

**Debt**: 10min

#### Noncompliant Code:

```kotlin
class MyIterator : Iterator<String> {

    override fun next(): String {
        return ""
    }
}
```

#### Compliant Code:

```kotlin
class MyIterator : Iterator<String> {

    override fun next(): String {
        if (!this.hasNext()) {
            throw NoSuchElementException()
        }
        // ...
    }
}
```

### LateinitUsage

Reports usages of the lateinit modifier.

Using lateinit for property initialization can be error-prone and the actual initialization is not
guaranteed. Try using constructor injection or delegation to initialize properties.

**Active by default**: No

**Debt**: 20min

#### Configuration options:

* ~~``excludeAnnotatedProperties``~~ (default: ``[]``)

  **Deprecated**: Use `ignoreAnnotated` instead

  Allows you to provide a list of annotations that disable this check.

* ``ignoreOnClassesPattern`` (default: ``''``)

  Allows you to disable the rule for a list of classes

#### Noncompliant Code:

```kotlin
class Foo {
    private lateinit var i1: Int
    lateinit var i2: Int
}
```

### MapGetWithNotNullAssertionOperator

Reports calls of the map access methods `map[]` or `map.get()` with a not-null assertion operator `!!`.
This may result in a NullPointerException.
Preferred access methods are `map[]` without `!!`, `map.getValue()`, `map.getOrDefault()` or `map.getOrElse()`.

Based on an IntelliJ IDEA inspection MapGetWithNotNullAssertionOperatorInspection.

**Active by default**: Yes - Since v1.21.0

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val map = emptyMap<String, String>()
map["key"]!!

val map = emptyMap<String, String>()
map.get("key")!!
```

#### Compliant Code:

```kotlin
val map = emptyMap<String, String>()
map["key"]

val map = emptyMap<String, String>()
map.getValue("key")

val map = emptyMap<String, String>()
map.getOrDefault("key", "")

val map = emptyMap<String, String>()
map.getOrElse("key", { "" })
```

### MissingPackageDeclaration

Reports when the package declaration is missing.

**Active by default**: No

**Debt**: 5min

### ~~MissingWhenCase~~

Rule deprecated as compiler performs this check by default

Turn on this rule to flag `when` expressions that do not check that all cases are covered when the subject is an enum
or sealed class and the `when` expression is used as a statement.

When this happens it's unclear what was intended when an unhandled case is reached. It is better to be explicit and
either handle all cases or use a default `else` statement to cover the unhandled cases.

**Active by default**: Yes - Since v1.2.0

**Requires Type Resolution**

**Debt**: 20min

#### Configuration options:

* ``allowElseExpression`` (default: ``true``)

  whether `else` can be treated as a valid case for enums and sealed classes

#### Noncompliant Code:

```kotlin
enum class Color {
    RED,
    GREEN,
    BLUE
}

fun whenOnEnumFail(c: Color) {
    when(c) {
        Color.BLUE -> {}
        Color.GREEN -> {}
    }
}
```

#### Compliant Code:

```kotlin
enum class Color {
    RED,
    GREEN,
    BLUE
}

fun whenOnEnumCompliant(c: Color) {
    when(c) {
        Color.BLUE -> {}
        Color.GREEN -> {}
        Color.RED -> {}
    }
}

fun whenOnEnumCompliant2(c: Color) {
    when(c) {
        Color.BLUE -> {}
        else -> {}
    }
}
```

### NullCheckOnMutableProperty

Reports null-checks on mutable properties, as these properties' value can be
changed - and thus make the null-check invalid - after the execution of the
if-statement.

**Active by default**: No

**Requires Type Resolution**

**Debt**: 10min

#### Noncompliant Code:

```kotlin
class A(private var a: Int?) {
fun foo() {
    if (a != null) {
      println(2 + a!!)
    }
}
}
```

#### Compliant Code:

```kotlin
class A(private val a: Int?) {
fun foo() {
    if (a != null) {
      println(2 + a)
    }
}
}
```

### NullableToStringCall

Reports `toString()` calls with a nullable receiver that may return the string "null".

**Active by default**: No

**Requires Type Resolution**

**Debt**: 5min

#### Noncompliant Code:

```kotlin
fun foo(a: Any?): String {
    return a.toString()
}

fun bar(a: Any?): String {
    return "$a"
}
```

#### Compliant Code:

```kotlin
fun foo(a: Any?): String {
    return a?.toString() ?: "-"
}

fun bar(a: Any?): String {
    return "${a ?: "-"}"
}
```

### PropertyUsedBeforeDeclaration

Reports properties that are used before declaration.

**Active by default**: No

**Requires Type Resolution**

**Debt**: 5min

#### Noncompliant Code:

```kotlin
class C {
    private val number
        get() = if (isValid) 1 else 0

    val list = listOf(number)

    private val isValid = true
}

fun main() {
    println(C().list) // [0]
}
```

#### Compliant Code:

```kotlin
class C {
    private val isValid = true

    private val number
        get() = if (isValid) 1 else 0

    val list = listOf(number)
}

fun main() {
    println(C().list) // [1]
}
```

### ~~RedundantElseInWhen~~

Rule deprecated as compiler performs this check by default

Reports `when` expressions that contain a redundant `else` case. This occurs when it can be
verified that all cases are already covered when checking cases on an enum or sealed class.

**Active by default**: Yes - Since v1.2.0

**Requires Type Resolution**

**Debt**: 5min

#### Noncompliant Code:

```kotlin
enum class Color {
    RED,
    GREEN,
    BLUE
}

fun whenOnEnumFail(c: Color) {
    when(c) {
        Color.BLUE -> {}
        Color.GREEN -> {}
        Color.RED -> {}
        else -> {}
    }
}
```

#### Compliant Code:

```kotlin
enum class Color {
    RED,
    GREEN,
    BLUE
}

fun whenOnEnumCompliant(c: Color) {
    when(c) {
        Color.BLUE -> {}
        Color.GREEN -> {}
        else -> {}
    }
}

fun whenOnEnumCompliant2(c: Color) {
    when(c) {
        Color.BLUE -> {}
        Color.GREEN -> {}
        Color.RED -> {}
    }
}
```

### UnconditionalJumpStatementInLoop

Reports loops which contain jump statements that jump regardless of any conditions.
This implies that the loop is only executed once and thus could be rewritten without a
loop altogether.

**Active by default**: No

**Debt**: 10min

#### Noncompliant Code:

```kotlin
for (i in 1..2) break
```

#### Compliant Code:

```kotlin
for (i in 1..2) {
    if (i == 1) break
}
```

### UnnecessaryNotNullCheck

Reports unnecessary not-null checks with `requireNotNull` or `checkNotNull` that can be removed by the user.

**Active by default**: No

**Requires Type Resolution**

**Debt**: 5min

#### Noncompliant Code:

```kotlin
var string = "foo"
println(requireNotNull(string))
```

#### Compliant Code:

```kotlin
var string : String? = "foo"
println(requireNotNull(string))
```

### UnnecessaryNotNullOperator

Reports unnecessary not-null operator usage (!!) that can be removed by the user.

**Active by default**: Yes - Since v1.16.0

**Requires Type Resolution**

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val a = 1
val b = a!!
```

#### Compliant Code:

```kotlin
val a = 1
val b = a
```

### UnnecessarySafeCall

Reports unnecessary safe call operators (`?.`) that can be removed by the user.

**Active by default**: Yes - Since v1.16.0

**Requires Type Resolution**

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val a: String = ""
val b = a?.length
```

#### Compliant Code:

```kotlin
val a: String? = null
val b = a?.length
```

### UnreachableCatchBlock

Reports unreachable catch blocks.
Catch blocks can be unreachable if the exception has already been caught in the block above.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

**Debt**: 5min

#### Noncompliant Code:

```kotlin
fun test() {
    try {
        foo()
    } catch (t: Throwable) {
        bar()
    } catch (e: Exception) {
        // Unreachable
        baz()
    }
}
```

#### Compliant Code:

```kotlin
fun test() {
    try {
        foo()
    } catch (e: Exception) {
        baz()
    } catch (t: Throwable) {
        bar()
    }
}
```

### UnreachableCode

Reports unreachable code.
Code can be unreachable because it is behind return, throw, continue or break expressions.
This unreachable code should be removed as it serves no purpose.

**Active by default**: Yes - Since v1.0.0

**Requires Type Resolution**

**Debt**: 10min

#### Noncompliant Code:

```kotlin
for (i in 1..2) {
    break
    println() // unreachable
}

throw IllegalArgumentException()
println() // unreachable

fun f() {
    return
    println() // unreachable
}
```

### UnsafeCallOnNullableType

Reports unsafe calls on nullable types. These calls will throw a NullPointerException in case
the nullable value is null. Kotlin provides many ways to work with nullable types to increase
null safety. Guard the code appropriately to prevent NullPointerExceptions.

**Active by default**: Yes - Since v1.2.0

**Requires Type Resolution**

**Debt**: 20min

#### Noncompliant Code:

```kotlin
fun foo(str: String?) {
    println(str!!.length)
}
```

#### Compliant Code:

```kotlin
fun foo(str: String?) {
    println(str?.length)
}
```

### UnsafeCast

Reports casts that will never succeed.

**Active by default**: Yes - Since v1.16.0

**Requires Type Resolution**

**Debt**: 20min

**Aliases**: UNCHECKED_CAST

#### Noncompliant Code:

```kotlin
fun foo(s: String) {
    println(s as Int)
}

fun bar(s: String) {
    println(s as? Int)
}
```

#### Compliant Code:

```kotlin
fun foo(s: Any) {
    println(s as Int)
}
```

### UnusedUnaryOperator

Detects unused unary operators.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val x = 1 + 2
    + 3 + 4
println(x) // 3
```

#### Compliant Code:

```kotlin
val x = 1 + 2 + 3 + 4
println(x) // 10
```

### UselessPostfixExpression

Reports postfix expressions (++, --) which are unused and thus unnecessary.
This leads to confusion as a reader of the code might think the value will be incremented/decremented.
However, the value is replaced with the original value which might lead to bugs.

**Active by default**: Yes - Since v1.21.0

**Debt**: 20min

#### Noncompliant Code:

```kotlin
var i = 0
i = i--
i = 1 + i++
i = i++ + 1

fun foo(): Int {
    var i = 0
    // ...
    return i++
}
```

#### Compliant Code:

```kotlin
var i = 0
i--
i = i + 2
i = i + 2

fun foo(): Int {
    var i = 0
    // ...
    i++
    return i
}
```

### WrongEqualsTypeParameter

Reports equals() methods which take in a wrongly typed parameter.
Correct implementations of the equals() method should only take in a parameter of type Any?
See: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html

**Active by default**: Yes - Since v1.2.0

**Debt**: 10min

#### Noncompliant Code:

```kotlin
class Foo {

    fun equals(other: String): Boolean {
        return super.equals(other)
    }
}
```

#### Compliant Code:

```kotlin
class Foo {

    fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
}
```
