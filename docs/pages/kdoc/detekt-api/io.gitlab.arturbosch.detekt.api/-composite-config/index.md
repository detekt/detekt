---
title: CompositeConfig - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [CompositeConfig](./index.html)

# CompositeConfig

`class CompositeConfig : `[`Config`](../-config/index.html)

Wraps two different configuration which should be considered when retrieving properties.

### Constructors

| [&lt;init&gt;](-init-.html) | Wraps two different configuration which should be considered when retrieving properties.`CompositeConfig(lookFirst: `[`Config`](../-config/index.html)`, lookSecond: `[`Config`](../-config/index.html)`)` |

### Functions

| [subConfig](sub-config.html) | Tries to retrieve part of the configuration based on given key.`fun subConfig(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Config`](../-config/index.html) |
| [toString](to-string.html) | `fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [valueOrDefault](value-or-default.html) | Retrieves a sub configuration or value based on given key. If configuration property cannot be found the specified default value is returned.`fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrDefault(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: T): T` |
| [valueOrNull](value-or-null.html) | Retrieves a sub configuration or value based on given key. If the configuration property cannot be found, null is returned.`fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrNull(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): T?` |

