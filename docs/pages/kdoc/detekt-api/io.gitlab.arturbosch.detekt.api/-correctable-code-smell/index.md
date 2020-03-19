---
title: CorrectableCodeSmell - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [CorrectableCodeSmell](./index.html)

# CorrectableCodeSmell

`open class CorrectableCodeSmell : `[`CodeSmell`](../-code-smell/index.html)

Represents a code smell for that can be auto corrected.

**See Also**

[CodeSmell](../-code-smell/index.html)

### Constructors

| [&lt;init&gt;](-init-.html) | Represents a code smell for that can be auto corrected.`CorrectableCodeSmell(issue: `[`Issue`](../-issue/index.html)`, entity: `[`Entity`](../-entity/index.html)`, message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, metrics: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Metric`](../-metric/index.html)`> = listOf(), references: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Entity`](../-entity/index.html)`> = listOf(), autoCorrectEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`)` |

### Properties

| [autoCorrectEnabled](auto-correct-enabled.html) | `val autoCorrectEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Functions

| [toString](to-string.html) | `open fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

