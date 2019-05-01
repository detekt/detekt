---
title: HasEntity - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [HasEntity](./index.html)

# HasEntity

`interface HasEntity`

Describes a source code position.

### Properties

| [charPosition](char-position.html) | `open val charPosition: `[`TextLocation`](../-text-location/index.html) |
| [entity](entity.html) | `abstract val entity: `[`Entity`](../-entity/index.html) |
| [file](file.html) | `open val file: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [inClass](in-class.html) | `open val inClass: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [location](location.html) | `open val location: `[`Location`](../-location/index.html) |
| [locationAsString](location-as-string.html) | `open val locationAsString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [name](name.html) | `open val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [signature](signature.html) | `open val signature: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [startPosition](start-position.html) | `open val startPosition: `[`SourceLocation`](../-source-location/index.html) |

### Inheritors

| [Finding](../-finding/index.html) | `interface Finding : `[`Compactable`](../-compactable/index.html)`, `[`HasEntity`](./index.html)`, `[`HasMetrics`](../-has-metrics/index.html)<br>Base interface of detection findings. Inherits a bunch of useful behaviour from sub interfaces. |

