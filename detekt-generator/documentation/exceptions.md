# exceptions

Rules in this rule set report issues related to how code throws and handles Exceptions.

## Content

1. [TooGenericExceptionCaught](#TooGenericExceptionCaught)
2. [ExceptionRaisedInUnexpectedLocation](#ExceptionRaisedInUnexpectedLocation)
3. [TooGenericExceptionThrown](#TooGenericExceptionThrown)
4. [NotImplementedDeclaration](#NotImplementedDeclaration)
5. [PrintStackTrace](#PrintStackTrace)
6. [InstanceOfCheckForException](#InstanceOfCheckForException)
7. [ThrowingExceptionsWithoutMessageOrCause](#ThrowingExceptionsWithoutMessageOrCause)
8. [ReturnFromFinally](#ReturnFromFinally)
9. [ThrowingExceptionFromFinally](#ThrowingExceptionFromFinally)
10. [ThrowingExceptionInMain](#ThrowingExceptionInMain)
11. [RethrowCaughtException](#RethrowCaughtException)
12. [ThrowingNewInstanceOfSameException](#ThrowingNewInstanceOfSameException)
13. [SwallowedException](#SwallowedException)
## Rules in the `exceptions` rule set:

### TooGenericExceptionCaught

TODO: Specify description

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
    } catch(e: Exception) { } // too generic exception thrown here
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

### ExceptionRaisedInUnexpectedLocation

TODO: Specify description

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

### TooGenericExceptionThrown

TODO: Specify description

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
        throw Exception()
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

### NotImplementedDeclaration

TODO: Specify description

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

TODO: Specify description

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

### InstanceOfCheckForException

TODO: Specify description

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

### ThrowingExceptionsWithoutMessageOrCause

TODO: Specify description

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

### ReturnFromFinally

TODO: Specify description

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

### ThrowingExceptionFromFinally

TODO: Specify description

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

TODO: Specify description

#### Noncompliant Code:

```kotlin
fun main(args: Array<String>) {
    throw new IOException()
}
```

### RethrowCaughtException

TODO: Specify description

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

### ThrowingNewInstanceOfSameException

TODO: Specify description

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

### SwallowedException

TODO: Specify description

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
