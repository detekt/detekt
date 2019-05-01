---
title: ThresholdedCodeSmell - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [ThresholdedCodeSmell](./index.html)

# ThresholdedCodeSmell

`open class ThresholdedCodeSmell : `[`CodeSmell`](../-code-smell/index.html)

Represents a code smell for which a specific metric can be determined which is responsible
for the existence of this rule violation.

### Constructors

| [&lt;init&gt;](-init-.html) | `ThresholdedCodeSmell(issue: `[`Issue`](../-issue/index.html)`, entity: `[`Entity`](../-entity/index.html)`, metric: `[`Metric`](../-metric/index.html)`, message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, references: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Entity`](../-entity/index.html)`> = emptyList())`<br>Represents a code smell for which a specific metric can be determined which is responsible for the existence of this rule violation. |

### Properties

| [metric](metric.html) | `val metric: `[`Metric`](../-metric/index.html) |
| [threshold](threshold.html) | `val threshold: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [value](value.html) | `val value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Inherited Properties

| [entity](../-code-smell/entity.html) | `open val entity: `[`Entity`](../-entity/index.html) |
| [id](../-code-smell/id.html) | `open val id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [issue](../-code-smell/issue.html) | `val issue: `[`Issue`](../-issue/index.html) |
| [message](../-code-smell/message.html) | `open val message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [metrics](../-code-smell/metrics.html) | `open val metrics: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Metric`](../-metric/index.html)`>` |
| [references](../-code-smell/references.html) | `open val references: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Entity`](../-entity/index.html)`>` |

### Functions

| [compact](compact.html) | `open fun compact(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [messageOrDescription](message-or-description.html) | `open fun messageOrDescription(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inherited Functions

| [compactWithSignature](../-code-smell/compact-with-signature.html) | `open fun compactWithSignature(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [toString](../-code-smell/to-string.html) | `open fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

