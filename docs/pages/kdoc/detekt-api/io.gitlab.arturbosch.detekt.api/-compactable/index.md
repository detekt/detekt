---
title: Compactable - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Compactable](./index.html)

# Compactable

`interface Compactable`

Provides a compact string representation.

### Functions

| [compact](compact.html) | Contract to format implementing object to a string representation.`abstract fun compact(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [compactWithSignature](compact-with-signature.html) | Same as [compact](compact.html) except the content should contain a substring which represents this exact findings via a custom identifier.`open fun compactWithSignature(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inheritors

| [Entity](../-entity/index.html) | Stores information about a specific code fragment.`data class Entity : `[`Compactable`](./index.html) |
| [Finding](../-finding/index.html) | Base interface of detection findings. Inherits a bunch of useful behaviour from sub interfaces.`interface Finding : `[`Compactable`](./index.html)`, `[`HasEntity`](../-has-entity/index.html)`, `[`HasMetrics`](../-has-metrics/index.html) |
| [Location](../-location/index.html) | Specifies a position within a source code fragment.`data class Location : `[`Compactable`](./index.html) |

