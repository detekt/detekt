---
title: Potential-bugs Rule Set
sidebar: home_sidebar
keywords: rules, potential-bugs
permalink: potential-bugs.html
toc: true
folder: documentation
---
The potential-bugs rule set provides rules that detect potential bugs.

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
    return this == other
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

val range = 2 until 1
```

#### Compliant Code:

```kotlin
for (i in 2..2) {}
for (i in 2 downTo 2) {}

val range =  2 until 2)
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

* `excludeAnnotatedProperties` (default: `""`)

   Allows you to provide a list of annotations that disable
this check.

* `ignoreOnClassesPattern` (default: `""`)

   Allows you to disable the rule for a list of classes

#### Noncompliant Code:

```kotlin
class Foo {
    @JvmField lateinit var i1: Int
    @JvmField @SinceKotlin("1.0.0") lateinit var i2: Int
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

Reports casts which are unsafe. In case the cast is not possible it will throw an exception.

**Severity**: Defect

**Debt**: 20min

#### Noncompliant Code:

```kotlin
fun foo(s: Any) {
    println(s as Int)
}
```

#### Compliant Code:

```kotlin
fun foo(s: Any) {
    println((s as? Int) ?: 0)
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
