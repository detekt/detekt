---
title: BaseConfig - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api.internal](../index.html) / [BaseConfig](./index.html)

# BaseConfig

`abstract class BaseConfig : `[`Config`](../../io.gitlab.arturbosch.detekt.api/-config/index.html)

Convenient base configuration which parses/casts the configuration value based on the type of the default value.

### Constructors

| [&lt;init&gt;](-init-.html) | Convenient base configuration which parses/casts the configuration value based on the type of the default value.`BaseConfig()` |

### Functions

| [tryParseBasedOnDefault](try-parse-based-on-default.html) | `open fun tryParseBasedOnDefault(result: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, defaultResult: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`): `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html) |
| [valueOrDefaultInternal](value-or-default-internal.html) | `open fun valueOrDefaultInternal(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, result: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?, default: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`): `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html) |

### Extension Functions

| [createPathFilters](../create-path-filters.html) | `fun `[`Config`](../../io.gitlab.arturbosch.detekt.api/-config/index.html)`.createPathFilters(): `[`PathFilters`](../-path-filters/index.html)`?` |
| [valueOrDefaultCommaSeparated](../value-or-default-comma-separated.html) | `fun `[`Config`](../../io.gitlab.arturbosch.detekt.api/-config/index.html)`.valueOrDefaultCommaSeparated(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |

### Inheritors

| [YamlConfig](../-yaml-config/index.html) | Config implementation using the yaml format. SubConfigurations can return sub maps according to the yaml specification.`class YamlConfig : `[`BaseConfig`](./index.html)`, `[`ValidatableConfiguration`](../-validatable-configuration/index.html) |

