---
title: Potential-bugs Rule Set
sidebar: home_sidebar
keywords: rules, potential-bugs
permalink: potential-bugs.html
toc: true
folder: documentation
---
The potential-bugs rule set provides rules that detect potential bugs.

### Deprecation

Deprecated elements are expected to be removed in future. Alternatives should be found if possible.

**Requires Type Resolution**

**Severity**: Defect

**Debt**: 20min

**Aliases**: DEPRECATION

### DuplicateCaseInWhenExpression

Flags duplicate case statements in when expressions.

If a when expression contains the same case statement multiple times they should be merged. Otherwise it might be
easy to miss one of the cases when reading the code, leading to unwanted side effects.

**Severity**: Warning

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

### EqualsAlwaysReturnsTrueOrFalse

Reports equals() methods which will always return true or false.

Equals methods should always report if some other object is equal to the current object.
See the Kotlin documentation for Any.equals(other: Any?):
https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/equals.html

**Severity**: Defect

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

**Severity**: Defect

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

### ExplicitGarbageCollectionCall

Reports all calls to explicitly trigger the Garbage Collector.
Code should work independently of the garbage collector and should not require the GC to be triggered in certain
points in time.

**Severity**: Defect

**Debt**: 20min

#### Noncompliant Code:

```kotlin
System.gc()
Runtime.getRuntime().gc()
System.runFinalization()
```

### HasPlatformType

Platform types must be declared explicitly in public APIs to prevent unexpected errors.

Based on code from Kotlin project:
https://github.com/JetBrains/kotlin/blob/1.3.50/idea/src/org/jetbrains/kotlin/idea/intentions/SpecifyTypeExplicitlyIntention.kt#L86-L107

**Requires Type Resolution**

**Severity**: Maintainability

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

**Requires Type Resolution**

**Severity**: Defect

**Debt**: 20min

#### Configuration options:

* ``restrictToAnnotatedMethods`` (default: ``true``)

   if the rule should check only annotated methods.

* ``returnValueAnnotations`` (default: ``['*.CheckReturnValue', '*.CheckResult']``)

   List of glob patterns to be used as inspection annotation

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

The default locale is almost always not appropriate for machine-readable text like HTTP headers.
For example, if locale with tag `ar-SA-u-nu-arab` is a current default then `%d` placeholders
will be evaluated to numbers consisting of Eastern-Arabic (non-ASCII) digits.
[java.util.Locale.US] is recommended for machine-readable output.

**Severity**: CodeSmell

**Debt**: 5min

#### Noncompliant Code:

```kotlin
String.format("Timestamp: %d", System.currentTimeMillis())

val str: String = getString()
str.toUpperCase()
str.toLowerCase()
```

#### Compliant Code:

```kotlin
String.format(Locale.US, "Timestamp: %d", System.currentTimeMillis())

val str: String = getString()
str.toUpperCase(Locale.US)
str.toLowerCase(Locale.US)
```

### ImplicitUnitReturnType

Functions using expression statements have an implicit return type.
Changing the type of the expression accidentally, changes the functions return type.
This may lead to backward incompatibility.
Use a block statement to make clear this function will never return a value.

**Requires Type Resolution**

**Severity**: Defect

**Debt**: 5min

#### Configuration options:

* ``allowExplicitReturnType`` (default: ``true``)

   if functions with explicit 'Unit' return type should be allowed


<noncompliant>
fun errorProneUnit() = println("Hello Unit")
fun errorProneUnitWithParam(param: String) = param.run { println(this) }
fun String.errorProneUnitWithReceiver() = run { println(this) }
</noncompliant>

<compliant>
fun blockStatementUnit() {
    // code
}

// explicit Unit is compliant by default; can be configured to enforce block statement
fun safeUnitReturn(): Unit = println("Hello Unit")
</compliant>

### InvalidRange

Reports ranges which are empty.
This might be a bug if it is used for instance as a loop condition. This loop will never be triggered then.
This might be due to invalid ranges like (10..9) which will cause the loop to never be entered.

**Severity**: Defect

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

**Severity**: Defect

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

**Severity**: Defect

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

Turn on this rule to flag usages of the lateinit modifier.

Using lateinit for property initialization can be error prone and the actual initialization is not
guaranteed. Try using constructor injection or delegation to initialize properties.

**Severity**: Defect

**Debt**: 20min

#### Configuration options:

* ``excludeAnnotatedProperties`` (default: ``[]``)

   Allows you to provide a list of annotations that disable
this check.

* ``ignoreOnClassesPattern`` (default: ``''``)

   Allows you to disable the rule for a list of classes

#### Noncompliant Code:

```kotlin
class Foo {
    @JvmField lateinit var i1: Int
    @JvmField @SinceKotlin("1.0.0") lateinit var i2: Int
}
```

### MapGetWithNotNullAssertionOperator

Reports calls of the map access methods `map[]` or `map.get()` with a not-null assertion operator `!!`.
This may result in a NullPointerException.
Preferred access methods are `map[]` without `!!`, `map.getValue()`, `map.getOrDefault()` or `map.getOrElse()`.

Based on an IntelliJ IDEA inspection MapGetWithNotNullAssertionOperatorInspection.

**Severity**: CodeSmell

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

### MissingWhenCase

Turn on this rule to flag `when` expressions that do not check that all cases are covered when the subject is an enum
or sealed class and the `when` expression is used as a statement.

When this happens it's unclear what was intended when an unhandled case is reached. It is better to be explicit and
either handle all cases or use a default `else` statement to cover the unhandled cases.

**Requires Type Resolution**

**Severity**: Defect

**Debt**: 20min

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

### NullableToStringCall

Turn on this rule to flag 'toString' calls with a nullable receiver that may return the string "null".

**Requires Type Resolution**

**Severity**: Defect

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

### RedundantElseInWhen

Turn on this rule to flag `when` expressions that contain a redundant `else` case. This occurs when it can be
verified that all cases are already covered when checking cases on an enum or sealed class.

Based on code from Kotlin compiler:
https://github.com/JetBrains/kotlin/blob/v1.3.30/compiler/frontend/src/org/jetbrains/kotlin/cfg/ControlFlowInformationProvider.kt

**Requires Type Resolution**

**Severity**: Warning

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

**Severity**: Defect

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

### UnnecessaryNotNullOperator

Reports unnecessary not-null operator usage (!!) that can be removed by the user.

**Requires Type Resolution**

**Severity**: Defect

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

Reports unnecessary safe call operators (`.?`) that can be removed by the user.

**Requires Type Resolution**

**Severity**: Defect

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val a: String = ""
val b = someValue?.length
```

#### Compliant Code:

```kotlin
val a: String? = null
val b = someValue?.length
```

### UnreachableCode

Reports unreachable code.
Code can be unreachable because it is behind return, throw, continue or break expressions.
This unreachable code should be removed as it serves no purpose.

**Severity**: Warning

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

**Requires Type Resolution**

**Severity**: Defect

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

**Requires Type Resolution**

**Severity**: Defect

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

### UselessPostfixExpression

This rule reports postfix expressions (++, --) which are unused and thus unnecessary.
This leads to confusion as a reader of the code might think the value will be incremented/decremented.
However the value is replaced with the original value which might lead to bugs.

**Severity**: Defect

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

**Severity**: Defect

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
