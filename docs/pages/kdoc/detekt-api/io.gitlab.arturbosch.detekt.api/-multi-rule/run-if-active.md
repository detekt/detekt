---
title: MultiRule.runIfActive - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [MultiRule](index.html) / [runIfActive](./run-if-active.html)

# runIfActive

`fun <T : `[`Rule`](../-rule/index.html)`> T.runIfActive(block: T.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Preferred way to run child rules because this composite rule
takes care of evaluating if a specific child should be run at all.

