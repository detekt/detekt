---
title: Coroutines Rule Set
sidebar: home_sidebar
keywords: [rules, coroutines]
permalink: coroutines.html
toc: true
folder: documentation
---
Rule Set ID: `coroutines`

The coroutines rule set analyzes code for potential coroutines problems.

### CoroutineLaunchedInTestWithoutRunTest

Detect coroutine launches from `@Test` functions outside a `runTest` block.
Launching coroutines in tests without using `runTest` could potentially swallow exceptions,
altering test results or causing crashes on other unrelated tests.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
@Test
fun `test that launches a coroutine`() {
    val scope = CoroutineScope(Dispatchers.Unconfined)
    scope.launch {
        suspendFunction()
    }
}
```

#### Compliant Code:

```kotlin
@Test
fun `test that launches a coroutine`() = runTest {
    launch {
        suspendFunction()
    }
}
```

### GlobalCoroutineUsage

Report usages of `GlobalScope.launch` and `GlobalScope.async`. It is highly discouraged by the Kotlin documentation:

> Global scope is used to launch top-level coroutines which are operating on the whole application lifetime and are
> not cancelled prematurely.

> Application code usually should use an application-defined CoroutineScope. Using async or launch on the instance
> of GlobalScope is highly discouraged.

See https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-global-scope/

**Active by default**: No

#### Noncompliant Code:

```kotlin
fun foo() {
    GlobalScope.launch { delay(1_000L) }
}
```

#### Compliant Code:

```kotlin
val scope = CoroutineScope(Dispatchers.Default)

fun foo() {
    scope.launch { delay(1_000L) }
}

fun onDestroy() {
   scope.cancel()
}
```

### InjectDispatcher

Always use dependency injection to inject dispatchers for easier testing.
This rule is based on the recommendation
https://developer.android.com/kotlin/coroutines/coroutines-best-practices#inject-dispatchers

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

#### Configuration options:

* ``dispatcherNames`` (default: ``['IO', 'Default', 'Unconfined']``)

  The names of dispatchers to detect by this rule

#### Noncompliant Code:

```kotlin
fun myFunc() {
    coroutineScope(Dispatchers.IO)
}
```

#### Compliant Code:

```kotlin
fun myFunc(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
    coroutineScope(dispatcher)
}

class MyRepository(dispatchers: CoroutineDispatcher = Dispatchers.IO)
```

### RedundantSuspendModifier

`suspend` modifier should only be used where needed, otherwise the function can only be used from other suspending
functions. This needlessly restricts use of the function and should be avoided by removing the `suspend` modifier
where it's not needed.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
suspend fun normalFunction() {
    println("string")
}
```

#### Compliant Code:

```kotlin
fun normalFunction() {
    println("string")
}
```

### SleepInsteadOfDelay

Report usages of `Thread.sleep` in suspending functions and coroutine blocks. A thread can
contain multiple coroutines at one time due to coroutines' lightweight nature, so if one
coroutine invokes `Thread.sleep`, it could potentially halt the execution of unrelated coroutines
and cause unpredictable behavior.

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
suspend fun foo() {
    Thread.sleep(1_000L)
}
```

#### Compliant Code:

```kotlin
suspend fun foo() {
    delay(1_000L)
}
```

### SuspendFunInFinallySection

Report usage of suspending functions within a `finally` section that are not enclosed in a non-cancellable context.
Without a non-cancellable context, these functions will not execute if the parent coroutine is cancelled.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
launch {
    try {
        suspendingWork()
    } finally {
        suspendingCleanup()
    }
}
```

#### Compliant Code:

```kotlin
launch {
    try {
        suspendingWork()
    } finally {
        withContext(NonCancellable) { suspendingCleanup() }
    }
}
```

### SuspendFunSwallowedCancellation

`suspend` functions should not be called inside `runCatching`'s lambda block, because `runCatching` catches all the
`Exception`s. For Coroutines to work in all cases, developers should make sure to propagate `CancellationException`
exceptions. This means `CancellationException` should never be:
* caught and swallowed (even if logged)
* caught and propagated to external systems
* caught and shown to the user

they must always be immediately rethrown in the same context.

Using `runCatching` increases this risk of mis-handling cancellation. If you catch and don't rethrow all the
`CancellationException` immediately, your coroutines are not cancelled even if you cancel their `CoroutineScope`.

This can very easily lead to:
* unexpected crashes
* extremely hard to diagnose bugs
* memory leaks
* performance issues
* battery drain

See reference, [Kotlin doc](https://kotlinlang.org/docs/cancellation-and-timeouts.html#cancellation-is-cooperative).

If your project wants to use `runCatching` and `Result` objects, it is recommended to write a `coRunCatching`
utility function which immediately re-throws `CancellationException`; and forbid `runCatching` and `suspend`
combinations by activating this rule.

**Active by default**: No

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
@Throws(IllegalStateException::class)
suspend fun bar(delay: Long) {
    check(delay <= 1_000L)
    delay(delay)
}

suspend fun foo() {
    runCatching {
        bar(1_000L)
    }
}

suspend fun baz() {
    try {
        bar(1_000L)
    } catch (e: IllegalStateException) {
        // catches CancellationException implicitly, since IllegalStateException is a super-class. Should be explicit
    }
}

suspend fun qux() {
    try {
        bar(1_000L)
    } catch (e: CancellationException) {
        doSomeWork() // potentially long-running bit of work before propagating the cancellation
        throw e
    }
}
```

#### Compliant Code:

```kotlin
@Throws(IllegalStateException::class)
suspend fun bar(delay: Long) {
    check(delay <= 1_000L)
    delay(delay)
}

suspend fun foo() {
    try {
        bar(1_000L)
    } catch (e: CancellationException) {
        throw e // explicitly caught and immediately re-thrown
    } catch (e: IllegalStateException) {
        // handle error
    }
}

// Alternate
@Throws(IllegalStateException::class)
suspend fun foo() {
    bar(1_000L)
}
```

### SuspendFunWithCoroutineScopeReceiver

Suspend functions that use `CoroutineScope` as receiver should not be marked as `suspend`.
A `CoroutineScope` provides structured concurrency via its `coroutineContext`. A `suspend`
function also has its own `coroutineContext`, which is now ambiguous and mixed with the
receiver`s.

See https://kotlinlang.org/docs/coroutines-basics.html#scope-builder-and-concurrency

**Active by default**: No

**Requires Type Resolution**

**Aliases**: SuspendFunctionOnCoroutineScope

#### Noncompliant Code:

```kotlin
suspend fun CoroutineScope.foo() {
    launch {
        delay(1.seconds)
    }
}
```

#### Compliant Code:

```kotlin
fun CoroutineScope.foo() {
    launch {
        delay(1.seconds)
    }
}

// Alternative
suspend fun foo() = coroutineScope {
    launch {
        delay(1.seconds)
    }
}
```

### SuspendFunWithFlowReturnType

Functions that return `Flow` from `kotlinx.coroutines.flow` should not be marked as `suspend`.
`Flows` are intended to be cold observable streams. The act of simply invoking a function that
returns a `Flow`, should not have any side effects. Only once collection begins against the
returned `Flow`, should work actually be done.

See https://kotlinlang.org/docs/flow.html#flows-are-cold

**Active by default**: Yes - Since v1.21.0

**Requires Type Resolution**

#### Noncompliant Code:

```kotlin
suspend fun observeSignals(): Flow<Unit> {
    val pollingInterval = getPollingInterval() // Done outside of the flow builder block.
    return flow {
        while (true) {
            delay(pollingInterval)
            emit(Unit)
        }
    }
}

private suspend fun getPollingInterval(): Long {
    // Return the polling interval from some repository
    // in a suspending manner.
}
```

#### Compliant Code:

```kotlin
fun observeSignals(): Flow<Unit> {
    return flow {
        val pollingInterval = getPollingInterval() // Moved into the flow builder block.
        while (true) {
            delay(pollingInterval)
            emit(Unit)
        }
    }
}

private suspend fun getPollingInterval(): Long {
    // Return the polling interval from some repository
    // in a suspending manner.
}
```
