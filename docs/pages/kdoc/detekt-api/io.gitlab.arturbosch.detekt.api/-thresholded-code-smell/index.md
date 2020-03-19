---
title: ThresholdedCodeSmell - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [ThresholdedCodeSmell](./index.html)

# ThresholdedCodeSmell

`open class ThresholdedCodeSmell : `[`CodeSmell`](../-code-smell/index.html)

Represents a code smell for which a specific metric can be determined which is responsible
for the existence of this rule violation.

**See Also**

[CodeSmell](../-code-smell/index.html)

### Constructors

| [&lt;init&gt;](-init-.html) | Represents a code smell for which a specific metric can be determined which is responsible for the existence of this rule violation.`ThresholdedCodeSmell(issue: `[`Issue`](../-issue/index.html)`, entity: `[`Entity`](../-entity/index.html)`, metric: `[`Metric`](../-metric/index.html)`, message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, references: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Entity`](../-entity/index.html)`> = emptyList())` |

### Properties

| [metric](metric.html) | `val metric: `[`Metric`](../-metric/index.html) |
| [threshold](threshold.html) | `val threshold: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [value](value.html) | `val value: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| [compact](compact.html) | Contract to format implementing object to a string representation.`open fun compact(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [messageOrDescription](message-or-description.html) | Explanation why this finding was raised.`open fun messageOrDescription(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

