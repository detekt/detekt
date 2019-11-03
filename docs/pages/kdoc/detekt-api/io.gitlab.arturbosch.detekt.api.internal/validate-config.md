---
title: validateConfig - detekt-api
---

[detekt-api](../index.html) / [io.gitlab.arturbosch.detekt.api.internal](index.html) / [validateConfig](./validate-config.html)

# validateConfig

`fun validateConfig(config: `[`Config`](../io.gitlab.arturbosch.detekt.api/-config/index.html)`, baseline: `[`Config`](../io.gitlab.arturbosch.detekt.api/-config/index.html)`, excludePatterns: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`Regex`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)`> = CommaSeparatedPattern(DEFAULT_PROPERTY_EXCLUDES).mapToRegex()): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Notification`](../io.gitlab.arturbosch.detekt.api/-notification/index.html)`>`