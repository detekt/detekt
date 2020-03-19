---
title: ConfigValidator - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [ConfigValidator](./index.html)

# ConfigValidator

`interface ConfigValidator : `[`Extension`](../-extension/index.html)

An extension which allows users to validate parts of the configuration.

Rule authors can validate if specific properties do appear in their config
or if their value lies in a specified range.

### Functions

| [validate](validate.html) | Executes queries on given config and reports any warnings or errors via [Notification](../-notification/index.html)s.`abstract fun validate(config: `[`Config`](../-config/index.html)`): `[`Collection`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)`<`[`Notification`](../-notification/index.html)`>` |

