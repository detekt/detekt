---
title: PropertiesAware - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [PropertiesAware](./index.html)

# PropertiesAware

`interface PropertiesAware`

Properties holder. Allows to store and retrieve any data.

### Properties

| [properties](properties.html) | Raw properties.`abstract val properties: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?>` |

### Functions

| [register](register.html) | Binds a given value with given key and stores it for later use.`abstract fun register(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, value: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Extension Functions

| [getOrNull](../get-or-null.html) | Allows to retrieve stored properties in a type safe way.`fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> `[`PropertiesAware`](./index.html)`.getOrNull(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): T?` |

### Inheritors

| [SetupContext](../-setup-context/index.html) | Context providing useful processing settings to initialize extensions.`interface SetupContext : `[`PropertiesAware`](./index.html) |

