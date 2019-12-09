---
title: FailFastConfig - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api.internal](../index.html) / [FailFastConfig](./index.html)

# FailFastConfig

`data class FailFastConfig : `[`Config`](../../io.gitlab.arturbosch.detekt.api/-config/index.html)`, `[`ValidatableConfiguration`](../-validatable-configuration/index.html)

### Constructors

| [&lt;init&gt;](-init-.html) | `FailFastConfig(originalConfig: `[`Config`](../../io.gitlab.arturbosch.detekt.api/-config/index.html)`, defaultConfig: `[`Config`](../../io.gitlab.arturbosch.detekt.api/-config/index.html)`)` |

### Functions

| [subConfig](sub-config.html) | Tries to retrieve part of the configuration based on given key.`fun subConfig(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`FailFastConfig`](./index.html) |
| [validate](validate.html) | `fun validate(baseline: `[`Config`](../../io.gitlab.arturbosch.detekt.api/-config/index.html)`, excludePatterns: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`Regex`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)`>): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Notification`](../../io.gitlab.arturbosch.detekt.api/-notification/index.html)`>` |
| [valueOrDefault](value-or-default.html) | Retrieves a sub configuration or value based on given key. If configuration property cannot be found the specified default value is returned.`fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrDefault(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: T): T` |
| [valueOrNull](value-or-null.html) | Retrieves a sub configuration or value based on given key. If the configuration property cannot be found, null is returned.`fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrNull(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): T?` |

