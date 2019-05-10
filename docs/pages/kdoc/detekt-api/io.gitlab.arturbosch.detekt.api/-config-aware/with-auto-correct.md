---
title: ConfigAware.withAutoCorrect - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [ConfigAware](index.html) / [withAutoCorrect](./with-auto-correct.html)

# withAutoCorrect

`open fun withAutoCorrect(block: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

If your rule supports to automatically correct the misbehaviour of underlying smell,
specify your code inside this method call, to allow the user of your rule to trigger auto correction
only when needed.

