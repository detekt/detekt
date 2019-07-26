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

| [&lt;init&gt;](-init-.html) | `CorrectableCodeSmell(issue: `[`Issue`](../-issue/index.html)`, entity: `[`Entity`](../-entity/index.html)`, message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, metrics: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Metric`](../-metric/index.html)`> = listOf(), references: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Entity`](../-entity/index.html)`> = listOf(), autoCorrectEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`)`<br>Represents a code smell for that can be auto corrected. |

### Properties

| [autoCorrectEnabled](auto-correct-enabled.html) | `val autoCorrectEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Inherited Properties

| [entity](../-code-smell/entity.html) | `open val entity: `[`Entity`](../-entity/index.html) |
| [id](../-code-smell/id.html) | `open val id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [issue](../-code-smell/issue.html) | `val issue: `[`Issue`](../-issue/index.html) |
| [message](../-code-smell/message.html) | `open val message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [metrics](../-code-smell/metrics.html) | `open val metrics: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Metric`](../-metric/index.html)`>` |
| [references](../-code-smell/references.html) | `open val references: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Entity`](../-entity/index.html)`>` |

### Functions

| [toString](to-string.html) | `open fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inherited Functions

| [compact](../-code-smell/compact.html) | `open fun compact(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [compactWithSignature](../-code-smell/compact-with-signature.html) | `open fun compactWithSignature(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [messageOrDescription](../-code-smell/message-or-description.html) | `open fun messageOrDescription(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

