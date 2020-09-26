---
title: Coroutines Rule Set
sidebar: home_sidebar
keywords: rules, coroutines
permalink: coroutines.html
toc: true
folder: documentation
---
The coroutines rule set analyzes code for potential coroutines problems.

### GlobalCoroutineUsage

Report usages of `GlobalScope.launch` and `GlobalScope.async`. It is highly discouraged by the Kotlin documentation:

> Global scope is used to launch top-level coroutines which are operating on the whole application lifetime and are
> not cancelled prematurely.

> Application code usually should use an application-defined CoroutineScope. Using async or launch on the instance
> of GlobalScope is highly discouraged.

See https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-global-scope/

**Severity**: Defect

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

### RedundantSuspendModifier

`suspend` modifier should only be used where needed, otherwise the function can only be used from other suspending
functions. This needlessly restricts use of the function and should be avoided by removing the `suspend` modifier
where it's not needed.

Based on code from Kotlin project:
https://github.com/JetBrains/kotlin/blob/v1.3.61/idea/src/org/jetbrains/kotlin/idea/inspections/RedundantSuspendModifierInspection.kt

**Requires Type Resolution**

**Severity**: Minor

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

### SuspendFunWithFlowReturnType

Functions that return `Flow` from `kotlinx.coroutines.flow` should not be marked as `suspend`.
`Flows` are intended to be cold observable streams. The act of simply invoking a function that
returns a `Flow`, should not have any side effects. Only once collection begins against the
returned `Flow`, should work actually be done.

See https://kotlinlang.org/docs/reference/coroutines/flow.html#flows-are-cold

**Requires Type Resolution**

**Severity**: Minor

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
