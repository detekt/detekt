---
title: YamlConfig - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [YamlConfig](./index.html)

# YamlConfig

`class YamlConfig : `[`BaseConfig`](../-base-config/index.html)

Config implementation using the yaml format. SubConfigurations can return sub maps according to the
yaml specification.

### Properties

| [parent](parent.html) | Returns the parent config which encloses this config part.`val parent: Parent?` |
| [properties](properties.html) | `val properties: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>` |

### Functions

| [subConfig](sub-config.html) | Tries to retrieve part of the configuration based on given key.`fun subConfig(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Config`](../-config/index.html) |
| [toString](to-string.html) | `fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [valueOrDefault](value-or-default.html) | Retrieves a sub configuration or value based on given key. If configuration property cannot be found the specified default value is returned.`fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrDefault(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: T): T` |
| [valueOrNull](value-or-null.html) | Retrieves a sub configuration or value based on given key. If the configuration property cannot be found, null is returned.`fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrNull(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): T?` |

### Companion Object Functions

| [load](load.html) | Factory method to load a yaml configuration. Given path must exist and end with "yml".`fun load(path: `[`Path`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html)`): `[`Config`](../-config/index.html) |
| [loadResource](load-resource.html) | Factory method to load a yaml configuration from a URL.`fun loadResource(url: `[`URL`](https://docs.oracle.com/javase/8/docs/api/java/net/URL.html)`): `[`Config`](../-config/index.html) |

