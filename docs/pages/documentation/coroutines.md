---
title: Coroutines Rule Set
sidebar: home_sidebar
keywords: rules, coroutines
permalink: coroutines.html
toc: true
folder: documentation
---
The coroutines rule set analyzes code for potential coroutines problems.

### GlobalScopeUsage

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
GlobalScope.launch { delay(1_000L) }
```

#### Compliant Code:

```kotlin
CoroutineScope(Dispatchers.Default).launch { delay(1_000L) }
```
