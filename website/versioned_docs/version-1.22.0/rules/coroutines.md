---
title: Coroutines Rule Set
sidebar: home_sidebar
keywords: [rules, coroutines]
permalink: coroutines.html
toc: true
folder: documentation
---
The coroutines rule set analyzes code for potential coroutines problems.

### GlobalCoroutineUsage

Report usages of `GlobalScope.launch` and `GlobalScope.async`. It is highly discouraged by the Kotlin documentation:

&gt; Global scope is used to launch top-level coroutines which are operating on the whole application lifetime and are
&gt; not cancelled prematurely.

&gt; Application code usually should use an application-defined CoroutineScope. Using async or launch on the instance
&gt; of GlobalScope is highly discouraged.

See https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-global-scope/

**Active by default**: No

**Debt**: 10min

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

**Debt**: 5min

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

**Debt**: 5min

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

**Debt**: 5min

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

### SuspendFunWithCoroutineScopeReceiver

Suspend functions that use `CoroutineScope` as receiver should not be marked as `suspend`.
A `CoroutineScope` provides structured concurrency via its `coroutineContext`. A `suspend`
function also has its own `coroutineContext`, which is now ambiguous and mixed with the
receiver`s.

See https://kotlinlang.org/docs/coroutines-basics.html#scope-builder-and-concurrency

**Active by default**: No

**Requires Type Resolution**

**Debt**: 10min

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

**Debt**: 10min

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
