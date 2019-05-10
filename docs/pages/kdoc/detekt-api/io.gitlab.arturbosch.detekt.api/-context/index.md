---
title: Context - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Context](./index.html)

# Context

`interface Context`

A context describes the storing and reporting mechanism of [Finding](../-finding/index.html)'s inside a [Rule](../-rule/index.html).
Additionally it handles suppression and aliases management.

The detekt engine retrieves the findings after each KtFile visit and resets the context
before the next KtFile.

**Author**
Artur Bosch

**Author**
Marvin Ramin

### Properties

| [findings](findings.html) | `abstract val findings: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>` |

### Functions

| [clearFindings](clear-findings.html) | `abstract fun clearFindings(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [report](report.html) | `abstract fun report(finding: `[`Finding`](../-finding/index.html)`, aliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = emptySet()): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`abstract fun report(findings: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>, aliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = emptySet()): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| [BaseRule](../-base-rule/index.html) | `abstract class BaseRule : `[`DetektVisitor`](../-detekt-visitor/index.html)`, `[`Context`](./index.html)<br>Defines the visiting mechanism for KtFile's. |
| [DefaultContext](../-default-context/index.html) | `open class DefaultContext : `[`Context`](./index.html)<br>Default [Context](./index.html) implementation. |

