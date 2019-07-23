---
title: Finding - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Finding](./index.html)

# Finding

`interface Finding : `[`Compactable`](../-compactable/index.html)`, `[`HasEntity`](../-has-entity/index.html)`, `[`HasMetrics`](../-has-metrics/index.html)

Base interface of detection findings. Inherits a bunch of useful behaviour
from sub interfaces.

Basic behaviour of a finding is that is can be assigned to an id and a source code position described as
an entity. Metrics and entity references can also considered for deeper characterization.

**Author**
Artur Bosch

### Properties

| [id](id.html) | `abstract val id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [issue](issue.html) | `abstract val issue: `[`Issue`](../-issue/index.html) |
| [message](message.html) | `abstract val message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [references](references.html) | `abstract val references: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Entity`](../-entity/index.html)`>` |

### Inherited Properties

| [charPosition](../-has-entity/char-position.html) | `open val charPosition: `[`TextLocation`](../-text-location/index.html) |
| [entity](../-has-entity/entity.html) | `abstract val entity: `[`Entity`](../-entity/index.html) |
| [file](../-has-entity/file.html) | `open val file: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [inClass](../-has-entity/in-class.html) | `open val inClass: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [location](../-has-entity/location.html) | `open val location: `[`Location`](../-location/index.html) |
| [locationAsString](../-has-entity/location-as-string.html) | `open val locationAsString: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [metrics](../-has-metrics/metrics.html) | `abstract val metrics: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Metric`](../-metric/index.html)`>` |
| [name](../-has-entity/name.html) | `open val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [signature](../-has-entity/signature.html) | `open val signature: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [startPosition](../-has-entity/start-position.html) | `open val startPosition: `[`SourceLocation`](../-source-location/index.html) |

### Functions

| [messageOrDescription](message-or-description.html) | `abstract fun messageOrDescription(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inherited Functions

| [compact](../-compactable/compact.html) | `abstract fun compact(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [compactWithSignature](../-compactable/compact-with-signature.html) | `open fun compactWithSignature(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [metricByType](../-has-metrics/metric-by-type.html) | `open fun metricByType(type: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Metric`](../-metric/index.html)`?` |

### Inheritors

| [CodeSmell](../-code-smell/index.html) | `open class CodeSmell : `[`Finding`](./index.html)<br>A code smell indicates any possible design problem inside a program's source code. The type of a code smell is described by an [Issue](../-issue/index.html). |

