---
title: HasMetrics - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [HasMetrics](./index.html)

# HasMetrics

`interface HasMetrics`

Adds metric container behaviour.

### Properties

| [metrics](metrics.html) | `abstract val metrics: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Metric`](../-metric/index.html)`>` |

### Functions

| [metricByType](metric-by-type.html) | `open fun metricByType(type: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Metric`](../-metric/index.html)`?` |

### Inheritors

| [Finding](../-finding/index.html) | `interface Finding : `[`Compactable`](../-compactable/index.html)`, `[`HasEntity`](../-has-entity/index.html)`, `[`HasMetrics`](./index.html)<br>Base interface of detection findings. Inherits a bunch of useful behaviour from sub interfaces. |

