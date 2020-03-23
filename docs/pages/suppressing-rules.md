---
title: "Suppressing Issues"
keywords: suppressing issues
sidebar: 
permalink: suppressing-rules.html
summary:
---

_detekt_ supports the Java (`@SuppressWarnings`) and Kotlin (`@Suppress`) style suppression. 
If both annotations are present, Kotlin's annotation is favored! 

To suppress an issue, the id of the issue must be written inside the values field of the annotation.
Furthermore, the ruleset plus rulename can be used to suppress issues (e.g. `@Suppress("LongMethod", "complexity.LongParameterList", ...)`).
The issue-id is also exactly the id of the reporting rule.

If a `LargeClass` is reported, but that is totally fine for you codebase, then just annotate it:

```kotlin
@Suppress("LargeClass") // or use complexity.LargeClass
object Constants {
    ...
}
```

Some rules like `TooManyFunctions` can be suppressed by using a file level annotation `@file:Suppress("TooManyFunctions")`.
