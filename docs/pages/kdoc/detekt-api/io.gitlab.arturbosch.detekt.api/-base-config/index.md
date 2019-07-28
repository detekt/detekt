---
title: BaseConfig - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [BaseConfig](./index.html)

# BaseConfig

`abstract class BaseConfig : `[`HierarchicalConfig`](../-hierarchical-config/index.html)

Convenient base configuration which parses/casts the configuration value based on the type of the default value.

### Constructors

| [&lt;init&gt;](-init-.html) | `BaseConfig()`<br>Convenient base configuration which parses/casts the configuration value based on the type of the default value. |

### Inherited Properties

| [parent](../-hierarchical-config/parent.html) | `abstract val parent: `[`HierarchicalConfig.Parent`](../-hierarchical-config/-parent/index.html)`?`<br>Returns the parent config which encloses this config part. |

### Functions

| [tryParseBasedOnDefault](try-parse-based-on-default.html) | `open fun tryParseBasedOnDefault(result: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, defaultResult: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`): `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html) |
| [valueOrDefaultInternal](value-or-default-internal.html) | `open fun valueOrDefaultInternal(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, result: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?, default: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`): `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html) |

### Inheritors

| [YamlConfig](../-yaml-config/index.html) | `class YamlConfig : `[`BaseConfig`](./index.html)<br>Config implementation using the yaml format. SubConfigurations can return sub maps according to the yaml specification. |

