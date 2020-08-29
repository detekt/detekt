---
title: DefaultContext - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [DefaultContext](./index.html)

# DefaultContext

`open class DefaultContext : `[`Context`](../-context/index.html)

Default [Context](../-context/index.html) implementation.

### Constructors

| [&lt;init&gt;](-init-.html) | Default [Context](../-context/index.html) implementation.`DefaultContext()` |

### Properties

| [findings](findings.html) | Returns a copy of violations for this rule.`open val findings: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>` |

### Functions

| [clearFindings](clear-findings.html) | Clears previous findings. Normally this is done on every new [KtFile](#) analyzed and should be called by clients.`fun clearFindings(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [report](report.html) | Reports a single code smell finding.`open fun report(finding: `[`Finding`](../-finding/index.html)`, aliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>, ruleSetId: `[`RuleSetId`](../-rule-set-id.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Reports a list of code smell findings.`open fun report(findings: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>, aliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>, ruleSetId: `[`RuleSetId`](../-rule-set-id.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

