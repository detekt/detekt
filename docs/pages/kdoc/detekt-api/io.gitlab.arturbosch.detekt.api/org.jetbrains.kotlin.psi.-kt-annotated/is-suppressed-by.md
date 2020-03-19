---
title: isSuppressedBy - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [org.jetbrains.kotlin.psi.KtAnnotated](index.html) / [isSuppressedBy](./is-suppressed-by.html)

# isSuppressedBy

`fun KtAnnotated.~~isSuppressedBy~~(id: `[`RuleId`](../-rule-id.html)`, aliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)
**Deprecated:** Moved to internal package. Should not be used outside of the rule context.

Checks if this kt element is suppressed by @Suppress or @SuppressWarnings annotations.

