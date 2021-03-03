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

## Formatting rules suppression

Please note that rules inside the [`formatting`](./formatting.html) ruleset can only be suppressed at **the file level**.

Rules inside this ruleset are wrappers around KtLint rules, and we don't have the same reporting capabilities that we offer for first party rules. For example, you can suppress the [MaximumLineLenght](formatting.html#maximumlinelength) rule only in your entire file with:

```kotlin
@file:Suppress("MaximumLineLenght")
package com.example

object AClassWithLongLines {
    //...
}
```

Several rules in the [`formatting`](./formatting.html) ruleset also have a "first party" counterpart. For instance you can use the [`MaxLineLength`](./style.html#maxlinelength) rule instead from the [`style`](./style.html) ruleset.

For those rules, you can suppress the inspection also locally (on top of an expression, function, class, etc.).
