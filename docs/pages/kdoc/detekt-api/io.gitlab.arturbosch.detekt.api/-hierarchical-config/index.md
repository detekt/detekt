---
title: HierarchicalConfig - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [HierarchicalConfig](./index.html)

# HierarchicalConfig

`interface HierarchicalConfig : `[`Config`](../-config/index.html)

A configuration which keeps track of the config it got sub-config'ed from by the [subConfig](../-config/sub-config.html) function.
It's main usage is to recreate the property-path which was taken when using the [subConfig](../-config/sub-config.html) function repeatedly.

### Types

| [Parent](-parent/index.html) | `data class Parent`<br>Keeps track of which key was taken to [subConfig](../-config/sub-config.html) this configuration. |

### Properties

| [parent](parent.html) | `abstract val parent: `[`HierarchicalConfig.Parent`](-parent/index.html)`?`<br>Returns the parent config which encloses this config part. |

### Inherited Functions

| [subConfig](../-config/sub-config.html) | `abstract fun subConfig(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Config`](../-config/index.html)<br>Tries to retrieve part of the configuration based on given key. |
| [valueOrDefault](../-config/value-or-default.html) | `abstract fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrDefault(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: `[`T`](../-config/value-or-default.html#T)`): `[`T`](../-config/value-or-default.html#T)<br>Retrieves a sub configuration or value based on given key. If configuration property cannot be found the specified default value is returned. |
| [valueOrNull](../-config/value-or-null.html) | `abstract fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrNull(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`T`](../-config/value-or-null.html#T)`?`<br>Retrieves a sub configuration or value based on given key. If the configuration property cannot be found, null is returned. |

### Inheritors

| [BaseConfig](../-base-config/index.html) | `abstract class BaseConfig : `[`HierarchicalConfig`](./index.html)<br>Convenient base configuration which parses/casts the configuration value based on the type of the default value. |

