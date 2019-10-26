---
title: isSuppressedBy - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api.internal](../index.html) / [org.jetbrains.kotlin.psi.KtAnnotated](index.html) / [isSuppressedBy](./is-suppressed-by.html)

# isSuppressedBy

`fun KtAnnotated.isSuppressedBy(id: `[`RuleId`](../../io.gitlab.arturbosch.detekt.api/-rule-id.html)`, aliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>, ruleSetId: `[`RuleSetId`](../../io.gitlab.arturbosch.detekt.api/-rule-set-id.html)`? = null): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Checks if this kt element is suppressed by @Suppress or @SuppressWarnings annotations.

