---
title: isSuppressedBy - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [org.jetbrains.kotlin.psi.KtElement](index.html) / [isSuppressedBy](./is-suppressed-by.html)

# isSuppressedBy

`fun KtElement.~~isSuppressedBy~~(id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, aliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)
**Deprecated:** Moved to internal package. Should not be used outside of the rule context.

Checks if this psi element is suppressed by @Suppress or @SuppressWarnings annotations.
If this element cannot have annotations, the first annotative parent is searched.

