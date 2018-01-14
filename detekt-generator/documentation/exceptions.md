# exceptions

Rules in this rule set report issues related to how code throws and handles Exceptions.

## Content

1. [ExceptionRaisedInUnexpectedLocation](#exceptionraisedinunexpectedlocation)
2. [InstanceOfCheckForException](#instanceofcheckforexception)
3. [NotImplementedDeclaration](#notimplementeddeclaration)
4. [PrintStackTrace](#printstacktrace)
5. [RethrowCaughtException](#rethrowcaughtexception)
6. [ReturnFromFinally](#returnfromfinally)
7. [SwallowedException](#swallowedexception)
8. [ThrowingExceptionFromFinally](#throwingexceptionfromfinally)
9. [ThrowingExceptionInMain](#throwingexceptioninmain)
10. [ThrowingExceptionsWithoutMessageOrCause](#throwingexceptionswithoutmessageorcause)
11. [ThrowingNewInstanceOfSameException](#throwingnewinstanceofsameexception)
12. [TooGenericExceptionCaught](#toogenericexceptioncaught)
13. [TooGenericExceptionThrown](#toogenericexceptionthrown)
## Rules in the `exceptions` rule set:

### ExceptionRaisedInUnexpectedLocation

This rule allows to define functions which should never throw an exception. If a function exists that does throw
an exception it will be reported. By default this rule is checking for `toString`, `hashCode, `equals` and
`finalize`. This rule is configurable via the `methodNames` configuration to change the list of functions which
should not throw any exceptions.

#### Configuration options:

* `methodNames` (default: `'toString,hashCode,equals,finalize'`)

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

This rule reports `catch` blocks which check for the type of an exception via `is` checks or casts.
Instead of catching generic exception types and then checking for specific exception types the code should
use multiple catch blocks. These catch blocks should then catch the specific exceptions.

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
```

### NotImplementedDeclaration

This rule reports all exceptions of the type `NotImplementedError` that are thrown. It also reports all `TODO(..)`
functions.
These indicate that functionality is still under development and will not work properly. Both of these should only
serve as temporary declarations and should not be put into production environments.

#### Noncompliant Code:

```kotlin
fun foo() {
    throw NotImplementedError()
}

fun todo() {
    TODO("")
}
```

### PrintStackTrace

This rule reports code that tries to print the stacktrace of an exception. Instead of simply printing a stacktrace
a better logging solution should be used.

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
}
```

### ReturnFromFinally

Reports all `return` statements in `finally` blocks.
Using `return` statements in `finally` blocks can discard and hide exceptions that are thrown in the `try` block.

#### Noncompliant Code:

```kotlin
fun foo() {
    try {
        throw MyException()
    } finally {
        return // prevents MyException from being propagated
    }
}
```

### SwallowedException

Exceptions should not be swallowed. This rule reports all instances where exceptions are `caught` and not correctly
passed into a newly thrown exception.

#### Noncompliant Code:

```kotlin
fun foo() {
    try {
        // ...
    } catch(e: IOException) {
        throw MyException(e.message) // e is swallowed
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
}
```

### ThrowingExceptionFromFinally

This rule reports all cases where exceptions are thrown from a `finally` block. Throwing exceptions from a `finally`
block should be avoided as it can lead to confusion and discarded exceptions.

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

#### Configuration options:

* `exceptions` (default: `'IllegalArgumentException,IllegalStateException,IOException'`)

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

#### Configuration options:

* `exceptions` (default: `- ArrayIndexOutOfBoundsException
- Error
- Exception
- IllegalMonitorStateException
- NullPointerException
- IndexOutOfBoundsException
- RuntimeException
- Throwable`)

   exceptions which are too generic and should not be caught
(default:

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

#### Configuration options:

* `exceptions` (default: `- Error
- Exception
- NullPointerException
- Throwable
- RuntimeException`)

   exceptions which are too generic and should not be thrown
(default:

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
