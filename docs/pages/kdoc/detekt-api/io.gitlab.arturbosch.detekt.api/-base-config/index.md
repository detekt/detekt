---
title: BaseConfig - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [BaseConfig](./index.html)

# BaseConfig

`abstract class BaseConfig : `[`Config`](../-config/index.html)

Convenient base configuration which parses/casts the configuration value based on the type of the default value.

### Constructors

| [&lt;init&gt;](-init-.html) | `BaseConfig()`<br>Convenient base configuration which parses/casts the configuration value based on the type of the default value. |

### Functions

| [tryParseBasedOnDefault](try-parse-based-on-default.html) | `open fun tryParseBasedOnDefault(result: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, defaultResult: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`): `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html) |
| [valueOrDefaultInternal](value-or-default-internal.html) | `open fun valueOrDefaultInternal(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, result: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?, default: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`): `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html) |

### Inherited Functions

| [subConfig](../-config/sub-config.html) | `abstract fun subConfig(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Config`](../-config/index.html)<br>Tries to retrieve part of the configuration based on given key. |
| [valueOrDefault](../-config/value-or-default.html) | `abstract fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrDefault(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: `[`T`](../-config/value-or-default.html#T)`): `[`T`](../-config/value-or-default.html#T)<br>Retrieves a sub configuration or value based on given key. If configuration property cannot be found the specified default value is returned. |
| [valueOrNull](../-config/value-or-null.html) | `abstract fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrNull(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`T`](../-config/value-or-null.html#T)`?`<br>Retrieves a sub configuration or value based on given key. If the configuration property cannot be found, null is returned. |

### Inheritors

| [YamlConfig](../-yaml-config/index.html) | `class YamlConfig : `[`BaseConfig`](./index.html)<br>Config implementation using the yaml format. SubConfigurations can return sub maps according to the yaml specification. |

