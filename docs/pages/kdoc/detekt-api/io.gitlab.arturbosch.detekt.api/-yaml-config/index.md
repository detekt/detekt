---
title: YamlConfig - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [YamlConfig](./index.html)

# YamlConfig

`class YamlConfig : `[`BaseConfig`](../-base-config/index.html)

Config implementation using the yaml format. SubConfigurations can return sub maps according to the
yaml specification.

**Author**
Artur Bosch

### Properties

| [properties](properties.html) | `val properties: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>` |

### Functions

| [subConfig](sub-config.html) | `fun subConfig(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Config`](../-config/index.html)<br>Tries to retrieve part of the configuration based on given key. |
| [toString](to-string.html) | `fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [valueOrDefault](value-or-default.html) | `fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrDefault(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: `[`T`](value-or-default.html#T)`): `[`T`](value-or-default.html#T)<br>Retrieves a sub configuration or value based on given key. If configuration property cannot be found the specified default value is returned. |
| [valueOrNull](value-or-null.html) | `fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrNull(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`T`](value-or-null.html#T)`?`<br>Retrieves a sub configuration or value based on given key. If the configuration property cannot be found, null is returned. |

### Inherited Functions

| [tryParseBasedOnDefault](../-base-config/try-parse-based-on-default.html) | `open fun tryParseBasedOnDefault(result: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, defaultResult: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`): `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html) |
| [valueOrDefaultInternal](../-base-config/value-or-default-internal.html) | `open fun valueOrDefaultInternal(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, result: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?, default: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`): `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html) |

### Companion Object Functions

| [load](load.html) | `fun load(path: `[`Path`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html)`): `[`Config`](../-config/index.html)<br>Factory method to load a yaml configuration. Given path must exist and end with "yml". |
| [loadResource](load-resource.html) | `fun loadResource(url: `[`URL`](https://docs.oracle.com/javase/8/docs/api/java/net/URL.html)`): `[`Config`](../-config/index.html)<br>Factory method to load a yaml configuration from a URL. |

