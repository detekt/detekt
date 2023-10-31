---
title: Exceptions Rule Set
sidebar: home_sidebar
keywords: [rules, exceptions]
permalink: exceptions.html
toc: true
folder: documentation
---
Rules in this rule set report issues related to how code throws and handles Exceptions.

### ExceptionRaisedInUnexpectedLocation

This rule reports functions which should never throw an exception. If a function exists that does throw
an exception it will be reported. By default, this rule checks `toString`, `hashCode`, `equals` and
`finalize`. This rule is configurable via the `methodNames` configuration to change the list of functions which
should not throw any exceptions.

**Active by default**: Yes - Since v1.16.0

**Debt**: 20min

#### Configuration options:

* ``methodNames`` (default: ``['equals', 'finalize', 'hashCode', 'toString']``)

  methods which should not throw exceptions

#### Noncompliant Code:

```kotlin
class Foo {

    override fun toString(): String {
        throw IllegalStateException() // exception should not be thrown here
    }
}
```

### InstanceOfCheckForException

This rule reports `catch` blocks which check for the type of exception via `is` checks or casts.
Instead of catching generic exception types and then checking for specific exception types the code should
use multiple catch blocks. These catch blocks should then catch the specific exceptions.

**Active by default**: Yes - Since v1.21.0

**Debt**: 20min

#### Noncompliant Code:

```kotlin
fun foo() {
    try {
        // ... do some I/O
    } catch(e: IOException) {
        if (e is MyException || (e as MyException) != null) { }
    }
}
```

#### Compliant Code:

```kotlin
fun foo() {
    try {
        // ... do some I/O
    } catch(e: MyException) {
    } catch(e: IOException) {
    }
}
```

### NotImplementedDeclaration

This rule reports all exceptions of the type `NotImplementedError` that are thrown. It also reports all `TODO(..)`
functions.
These indicate that functionality is still under development and will not work properly. Both of these should only
serve as temporary declarations and should not be put into production environments.

**Active by default**: No

**Debt**: 20min

#### Noncompliant Code:

```kotlin
fun foo() {
    throw NotImplementedError()
}

fun todo() {
    TODO("")
}
```

### ObjectExtendsThrowable

This rule reports all `objects` including `companion objects` that extend any type of
`Throwable`. Throwable instances are not intended for reuse as they are stateful and contain
mutable information about a specific exception or error. Hence, global singleton `Throwables`
should be avoided.

See https://kotlinlang.org/docs/object-declarations.html#object-declarations-overview
See https://kotlinlang.org/docs/object-declarations.html#companion-objects

**Active by default**: No

**Requires Type Resolution**

**Debt**: 10min

#### Noncompliant Code:

```kotlin
object InvalidCredentialsException : Throwable()

object BanException : Exception()

object AuthException : RuntimeException()
```

#### Compliant Code:

```kotlin
class InvalidCredentialsException : Throwable()

class BanException : Exception()

class AuthException : RuntimeException()
```

### PrintStackTrace

This rule reports code that tries to print the stacktrace of an exception. Instead of simply printing a stacktrace
a better logging solution should be used.

**Active by default**: Yes - Since v1.16.0

**Debt**: 20min

#### Noncompliant Code:

```kotlin
fun foo() {
    Thread.dumpStack()
}

fun bar() {
    try {
        // ...
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
```

#### Compliant Code:

```kotlin
val LOGGER = Logger.getLogger()

fun bar() {
    try {
        // ...
    } catch (e: IOException) {
        LOGGER.info(e)
    }
}
```

### RethrowCaughtException

This rule reports all exceptions that are caught and then later re-thrown without modification.
It ignores cases:
1. When caught exceptions that are rethrown if there is work done before that.
2. When there are more than one catch in try block and at least one of them has some work.

**Active by default**: Yes - Since v1.16.0

**Debt**: 5min

#### Noncompliant Code:

```kotlin
fun foo() {
    try {
        // ...
    } catch (e: IOException) {
        throw e
    }
}
```

#### Compliant Code:

```kotlin
fun foo() {
    try {
        // ...
    } catch (e: IOException) {
        throw MyException(e)
    }
    try {
        // ...
    } catch (e: IOException) {
        print(e)
        throw e
    }
    try {
        // ...
    } catch (e: IOException) {
        print(e.message)
        throw e
    }

    try {
        // ...
    } catch (e: IOException) {
        throw e
    } catch (e: Exception) {
        print(e.message)
    }
}
```

### ReturnFromFinally

Reports all `return` statements in `finally` blocks.
Using `return` statements in `finally` blocks can discard and hide exceptions that are thrown in the `try` block.
Furthermore, this rule reports values from `finally` blocks, if the corresponding `try` is used as an expression.

**Active by default**: Yes - Since v1.16.0

**Requires Type Resolution**

**Debt**: 20min

#### Configuration options:

* ``ignoreLabeled`` (default: ``false``)

  ignores labeled return statements

#### Noncompliant Code:

```kotlin
fun foo() {
    try {
        throw MyException()
    } finally {
        return // prevents MyException from being propagated
    }
}

val a: String = try { "s" } catch (e: Exception) { "e" } finally { "f" }
```

### SwallowedException

Exceptions should not be swallowed. This rule reports all instances where exceptions are `caught` and not correctly
passed (e.g. as a cause) into a newly thrown exception.

The exception types configured in `ignoredExceptionTypes` indicate nonexceptional outcomes.
These by default configured exception types are part of Java.
Therefore, Kotlin developers have to handle them by using the catch clause.
For that reason, this rule ignores that these configured exception types are caught.

**Active by default**: Yes - Since v1.16.0

**Debt**: 20min

#### Configuration options:

* ``ignoredExceptionTypes`` (default: ``['InterruptedException', 'MalformedURLException', 'NumberFormatException', 'ParseException']``)

  exception types which should be ignored (both in the catch clause and body)

* ``allowedExceptionNameRegex`` (default: ``'_|(ignore|expected).*'``)

  ignores too generic exception types which match this regex

#### Noncompliant Code:

```kotlin
fun foo() {
    try {
        // ...
    } catch(e: IOException) {
        throw MyException(e.message) // e is swallowed
    }
    try {
        // ...
    } catch(e: IOException) {
        throw MyException() // e is swallowed
    }
    try {
        // ...
    } catch(e: IOException) {
        bar() // exception is unused
    }
}
```

#### Compliant Code:

```kotlin
fun foo() {
    try {
        // ...
    } catch(e: IOException) {
        throw MyException(e)
    }
    try {
        // ...
    } catch(e: IOException) {
        println(e) // logging is ok here
    }
}
```

### ThrowingExceptionFromFinally

This rule reports all cases where exceptions are thrown from a `finally` block. Throwing exceptions from a `finally`
block should be avoided as it can lead to confusion and discarded exceptions.

**Active by default**: Yes - Since v1.16.0

**Debt**: 20min

#### Noncompliant Code:

```kotlin
fun foo() {
    try {
        // ...
    } finally {
        throw IOException()
    }
}
```

### ThrowingExceptionInMain

This rule reports all exceptions that are thrown in a `main` method.
An exception should only be thrown if it can be handled by a "higher" function.

**Active by default**: No

**Debt**: 20min

#### Noncompliant Code:

```kotlin
fun main(args: Array<String>) {
    // ...
    throw IOException() // exception should not be thrown here
}
```

### ThrowingExceptionsWithoutMessageOrCause

This rule reports all exceptions which are thrown without arguments or further description.
Exceptions should always call one of the constructor overloads to provide a message or a cause.
Exceptions should be meaningful and contain as much detail about the error case as possible. This will help to track
down an underlying issue in a better way.

**Active by default**: Yes - Since v1.16.0

**Debt**: 5min

#### Configuration options:

* ``exceptions`` (default: ``['ArrayIndexOutOfBoundsException', 'Exception', 'IllegalArgumentException', 'IllegalMonitorStateException', 'IllegalStateException', 'IndexOutOfBoundsException', 'NullPointerException', 'RuntimeException', 'Throwable']``)

  exceptions which should not be thrown without message or cause

#### Noncompliant Code:

```kotlin
fun foo(bar: Int) {
    if (bar < 1) {
        throw IllegalArgumentException()
    }
    // ...
}
```

#### Compliant Code:

```kotlin
fun foo(bar: Int) {
    if (bar < 1) {
        throw IllegalArgumentException("bar must be greater than zero")
    }
    // ...
}
```

### ThrowingNewInstanceOfSameException

Exceptions should not be wrapped inside the same exception type and then rethrown. Prefer wrapping exceptions in more
meaningful exception types.

**Active by default**: Yes - Since v1.16.0

**Debt**: 5min

#### Noncompliant Code:

```kotlin
fun foo() {
    try {
        // ...
    } catch (e: IllegalStateException) {
        throw IllegalStateException(e) // rethrows the same exception
    }
}
```

#### Compliant Code:

```kotlin
fun foo() {
    try {
        // ...
    } catch (e: IllegalStateException) {
        throw MyException(e)
    }
}
```

### TooGenericExceptionCaught

This rule reports `catch` blocks for exceptions that have a type that is too generic.
It should be preferred to catch specific exceptions to the case that is currently handled. If the scope of the caught
exception is too broad it can lead to unintended exceptions being caught.

**Active by default**: Yes - Since v1.0.0

**Debt**: 20min

#### Configuration options:

* ``exceptionNames`` (default: ``['ArrayIndexOutOfBoundsException', 'Error', 'Exception', 'IllegalMonitorStateException', 'IndexOutOfBoundsException', 'NullPointerException', 'RuntimeException', 'Throwable']``)

  exceptions which are too generic and should not be caught

* ``allowedExceptionNameRegex`` (default: ``'_|(ignore|expected).*'``)

  ignores too generic exception types which match this regex

#### Noncompliant Code:

```kotlin
fun foo() {
    try {
        // ... do some I/O
    } catch(e: Exception) { } // too generic exception caught here
}
```

#### Compliant Code:

```kotlin
fun foo() {
    try {
        // ... do some I/O
    } catch(e: IOException) { }
}
```

### TooGenericExceptionThrown

This rule reports thrown exceptions that have a type that is too generic. It should be preferred to throw specific
exceptions to the case that has currently occurred.

**Active by default**: Yes - Since v1.0.0

**Debt**: 20min

#### Configuration options:

* ``exceptionNames`` (default: ``['Error', 'Exception', 'RuntimeException', 'Throwable']``)

  exceptions which are too generic and should not be thrown

#### Noncompliant Code:

```kotlin
fun foo(bar: Int) {
    if (bar < 1) {
        throw Exception() // too generic exception thrown here
    }
    // ...
}
```

#### Compliant Code:

```kotlin
fun foo(bar: Int) {
    if (bar < 1) {
        throw IllegalArgumentException("bar must be greater than zero")
    }
    // ...
}
```
