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

As stated in the Kotlin [documentation](https://kotlinlang.org/docs/arrays.html#primitive-type-arrays) Kotlin has
specialized arrays to represent primitive types without boxing overhead, such as `IntArray`, `ByteArray` and so on.

**Active by default**: Yes - Since v1.2.0

**Requires Type Resolution**

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

#### Configuration options:

* ``allowedOperations`` (default: ``2``)

  The maximum number of allowed chained collection operations.

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
See more details here:
[Exploring Kotlin Hidden Costs - Part 1](https://bladecoder.medium.com/exploring-kotlins-hidden-costs-part-1-fbb9935d9b62)
[Exploring Kotlin Hidden Costs - Part 2](https://bladecoder.medium.com/exploring-kotlins-hidden-costs-part-2-324a4a50b70)
[Exploring Kotlin Hidden Costs - Part 3](https://bladecoder.medium.com/exploring-kotlins-hidden-costs-part-3-3bf6e0dbf0a4)

To solve this code smell, the forEach usage should be replaced by a for loop.

**Active by default**: Yes - Since v1.0.0

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
[Exploring Kotlin Hidden Costs - Part 1](https://bladecoder.medium.com/exploring-kotlins-hidden-costs-part-1-fbb9935d9b62)
[Exploring Kotlin Hidden Costs - Part 2](https://bladecoder.medium.com/exploring-kotlins-hidden-costs-part-2-324a4a50b70)
[Exploring Kotlin Hidden Costs - Part 3](https://bladecoder.medium.com/exploring-kotlins-hidden-costs-part-3-3bf6e0dbf0a4)

The Kotlin compiler since v1.1.60 has an optimization that skips the array copy when an array constructor
function is used to create the arguments that are passed to the vararg parameter. This case will not be flagged
by the rule since it doesn't suffer the performance penalty of an array copy.

**Active by default**: Yes - Since v1.0.0

**Requires Type Resolution**

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

This rule applies to unnecessary binary expressions, including those in `if` and `when` conditions, as well as all predicates.
Binary expressions with `||` and `&&` operator are checked.

**Active by default**: No

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

#### Noncompliant Code:

```kotlin
val i = Integer(1).toString() // temporary Integer instantiation just for the conversion
```

#### Compliant Code:

```kotlin
val i = Integer.toString(1)
```

### UnnecessaryTypeCasting

Reports cast of unnecessary type casting. Cases like this can be
replaced with type checking for performance reasons.

**Active by default**: No

#### Noncompliant Code:

```kotlin
fun foo() {
val objList: List<Any> = emptyList()
objList.any { it as? String != null }
}
```

#### Compliant Code:

```kotlin
fun foo() {
val objList: List<Any> = emptyList()
objList.any { it is String }
}
```
