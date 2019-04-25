---
title: Compactable - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Compactable](./index.html)

# Compactable

`interface Compactable`

Provides a compact string representation.

### Functions

| [compact](compact.html) | `abstract fun compact(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [compactWithSignature](compact-with-signature.html) | `open fun compactWithSignature(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inheritors

| [Entity](../-entity/index.html) | `data class Entity : `[`Compactable`](./index.html)<br>Stores information about a specific code fragment. |
| [Finding](../-finding/index.html) | `interface Finding : `[`Compactable`](./index.html)`, `[`HasEntity`](../-has-entity/index.html)`, `[`HasMetrics`](../-has-metrics/index.html)<br>Base interface of detection findings. Inherits a bunch of useful behaviour from sub interfaces. |
| [Location](../-location/index.html) | `data class Location : `[`Compactable`](./index.html)<br>Specifies a position within a source code fragment. |

