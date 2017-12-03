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

### ExceptionRaisedInUnexpectedLocation

TODO: Specify description

#### Configuration options:

* `methodNames` (default: `'toString,hashCode,equals,finalize'`)

   methods which should not throw exceptions

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

### NotImplementedDeclaration

TODO: Specify description

### PrintStackTrace

TODO: Specify description

### InstanceOfCheckForException

TODO: Specify description

### ThrowingExceptionsWithoutMessageOrCause

TODO: Specify description

#### Configuration options:

* `exceptions` (default: `'IllegalArgumentException,IllegalStateException,IOException'`)

   exceptions which should not be thrown without message or cause

### ReturnFromFinally

TODO: Specify description

### ThrowingExceptionFromFinally

TODO: Specify description

### ThrowingExceptionInMain

TODO: Specify description

### RethrowCaughtException

TODO: Specify description

### ThrowingNewInstanceOfSameException

TODO: Specify description

### SwallowedException

TODO: Specify description
