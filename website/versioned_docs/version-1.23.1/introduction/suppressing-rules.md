---
id: suppressing-rules
title: "Suppressing Issues"
keywords: [suppressing, issues]
sidebar_position: 6
---

_detekt_ supports the Java (`@SuppressWarnings`) and Kotlin (`@Suppress`) style suppression. 
If both annotations are present, Kotlin's annotation is favored! 

To suppress an issue, the id of the rule must be written inside the values field of the annotation (e.g. `@Suppress("LongMethod")`).

If a `LargeClass` is reported, but that is totally fine for you codebase, then just annotate it:

```kotlin
@Suppress("LargeClass") // or use complexity.LargeClass
object Constants {
    ...
}
```

It is also possible to prefix the rule id with `detekt` and/or the ruleset id such as `@Suppress("detekt:LongMethod")` or `@Suppress("complexity:LongParameterList")`. 

The following table shows the various supported suppression formats.

| Example Suppression                                                                                             | Description                                               |
|-----------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------|
| `all`, `detekt:all`, `detekt.all`                                                                               | Suppresses all detekt findings.                           |
| `style`, `detekt:style`, `detekt.style`                                                                         | Suppresses all findings from rules in the style rule set. |
| `MagicNumber`, `style:MagicNumber`, `style.MagicNumber`, `detekt:style:MagicNumber`, `detekt.style.MagicNumber` | Suppresses all MagicNumber rule findings.                 |

Some rules like `TooManyFunctions` can only be suppressed by using a file level annotation `@file:Suppress("TooManyFunctions")`.

**Formatting rules suppression**

Please note that rules inside the [`formatting`](/docs/rules/formatting) ruleset can only be suppressed at **the file level**.

Rules inside this ruleset are wrappers around KtLint rules, and we don't have the same reporting capabilities that we offer for first party rules. For example, you can suppress the [MaximumLineLength](/docs/rules/formatting#maximumlinelength) rule only in your entire file with:

```kotlin
@file:Suppress("MaximumLineLength")

package com.example

object AClassWithLongLines {
    //...
}
```

Several rules in the [`formatting`](/docs/rules/formatting) ruleset also have a "first party" counterpart. For instance you can use the [`MaxLineLength`](/docs/rules/style#maxlinelength) rule instead from the [`style`](/docs/rules/style) ruleset.

For those rules, you can suppress the inspection also locally (on top of an expression, function, class, etc.).
