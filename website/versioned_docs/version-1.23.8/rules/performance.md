---
title: Performance Rule Set
sidebar: home_sidebar
keywords: [rules, performance]
permalink: performance.html
toc: true
folder: documentation
---
The performance rule set analyzes code for potential performance problems.

### ArrayPrimitive

Using `Array<Primitive>` leads to implicit boxing and performance hit. Prefer using Kotlin specialized Array
Instances.

As stated in the Kotlin [documentation](https://kotlinlang.org/docs/basic-types.html#arrays) Kotlin has
specialized arrays to represent primitive types without boxing overhead, such as `IntArray`, `ByteArray` and so on.

**Active by default**: Yes - Since v1.2.0

**Requires Type Resolution**

**Debt**: 5min

#### Noncompliant Code:

```kotlin
fun function(array: Array<Int>) { }

fun returningFunction(): Array<Double> { }
```

#### Compliant Code:

```kotlin
fun function(array: IntArray) { }

fun returningFunction(): DoubleArray { }
```

### CouldBeSequence

Long chains of collection operations will have a performance penalty due to a new list being created for each call. Consider using sequences instead. Read more about this in the [documentation](https://kotlinlang.org/docs/sequences.html)

**Active by default**: No

**Requires Type Resolution**

**Debt**: 5min

#### Configuration options:

* ``threshold`` (default: ``3``)

  the number of chained collection operations required to trigger rule

#### Noncompliant Code:

```kotlin
listOf(1, 2, 3, 4).map { it*2 }.filter { it < 4 }.map { it*it }
```

#### Compliant Code:

```kotlin
listOf(1, 2, 3, 4).asSequence().map { it*2 }.filter { it < 4 }.map { it*it }.toList()

listOf(1, 2, 3, 4).map { it*2 }
```

### ForEachOnRange

Using the forEach method on ranges has a heavy performance cost. Prefer using simple for loops.

Benchmarks have shown that using forEach on a range can have a huge performance cost in comparison to
simple for loops. Hence, in most contexts, a simple for loop should be used instead.
See more details here: https://sites.google.com/a/athaydes.com/renato-athaydes/posts/kotlins-hidden-costs-benchmarks
To solve this CodeSmell, the forEach usage should be replaced by a for loop.

**Active by default**: Yes - Since v1.0.0

**Debt**: 5min

#### Noncompliant Code:

```kotlin
(1..10).forEach {
    println(it)
}
(1 until 10).forEach {
    println(it)
}
(10 downTo 1).forEach {
    println(it)
}
```

#### Compliant Code:

```kotlin
for (i in 1..10) {
    println(i)
}
```

### SpreadOperator

In most cases using a spread operator causes a full copy of the array to be created before calling a method.
This has a very high performance penalty. Benchmarks showing this performance penalty can be seen here:
https://sites.google.com/a/athaydes.com/renato-athaydes/posts/kotlins-hidden-costs-benchmarks

The Kotlin compiler since v1.1.60 has an optimization that skips the array copy when an array constructor
function is used to create the arguments that are passed to the vararg parameter. When type resolution is enabled in
detekt this case will not be flagged by the rule since it doesn't suffer the performance penalty of an array copy.

**Active by default**: Yes - Since v1.0.0

**Debt**: 20min

#### Noncompliant Code:

```kotlin
val strs = arrayOf("value one", "value two")
val foo = bar(*strs)

fun bar(vararg strs: String) {
    strs.forEach { println(it) }
}
```

#### Compliant Code:

```kotlin
// array copy skipped in this case since Kotlin 1.1.60
val foo = bar(*arrayOf("value one", "value two"))

// array not passed so no array copy is required
val foo2 = bar("value one", "value two")

fun bar(vararg strs: String) {
    strs.forEach { println(it) }
}
```

### UnnecessaryPartOfBinaryExpression

Unnecessary binary expression add complexity to the code and accomplish nothing. They should be removed.
The rule works with all binary expression included if and when condition. The rule also works with all predicates.
The rule verify binary expression only in case when the expression use only one type of the following
operators || or &&.

**Active by default**: No

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val foo = true
val bar = true

if (foo || bar || foo) {
}
```

#### Compliant Code:

```kotlin
val foo = true
if (foo) {
}
```

### UnnecessaryTemporaryInstantiation

Avoid temporary objects when converting primitive types to String. This has a performance penalty when compared
to using primitive types directly.
To solve this issue, remove the wrapping type.

**Active by default**: Yes - Since v1.0.0

**Debt**: 5min

#### Noncompliant Code:

```kotlin
val i = Integer(1).toString() // temporary Integer instantiation just for the conversion
```

#### Compliant Code:

```kotlin
val i = Integer.toString(1)
```
