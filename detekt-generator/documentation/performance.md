# performance

The performance rule set analyzes code for potential performance problems.

## Content

1. [ForEachOnRange](#foreachonrange)
2. [SpreadOperator](#spreadoperator)
3. [UnnecessaryTemporaryInstantiation](#unnecessarytemporaryinstantiation)
## Rules in the `performance` rule set:

### ForEachOnRange

Using the forEach method on ranges has a heavy performance cost. Prefer using simple for loops.

Benchmarks have shown that using forEach on a range can have a huge performance cost in comparison to
simple for loops. Hence in most contexts a simple for loop should be used instead.
See more details here: https://sites.google.com/a/athaydes.com/renato-athaydes/posts/kotlinshiddencosts-benchmarks
To solve this CodeSmell, the forEach usage should be replaced by a for loop.

#### Noncompliant Code:

```kotlin
(1..10).forEach {
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

Using a spread operator causes a full copy of the array to be created before calling a method.
This has a very high performance penalty.
Benchmarks showing this performance penalty can be seen here:
https://sites.google.com/a/athaydes.com/renato-athaydes/posts/kotlinshiddencosts-benchmarks

#### Noncompliant Code:

```kotlin
fun foo(strs: Array<String>) {
    bar(*strs)
}

fun bar(vararg strs: String) {
    strs.forEach { println(it) }
}
```

### UnnecessaryTemporaryInstantiation

Avoid temporary objects when converting primitive types to String. This has a performance penalty when compared
to using primitive types directly.
To solve this issue, remove the wrapping type.

#### Noncompliant Code:

```kotlin
val i = Integer(1).toString() // temporary Integer instantiation just for the conversion
```

#### Compliant Code:

```kotlin
val i = Integer.toString(1)
```
