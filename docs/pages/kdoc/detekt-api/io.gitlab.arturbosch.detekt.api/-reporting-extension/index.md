---
title: ReportingExtension - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [ReportingExtension](./index.html)

# ReportingExtension

`interface ReportingExtension : `[`Extension`](../-extension/index.html)

Allows to intercept detekt's result container by listening to the initial and final state
and manipulate the reported findings.

### Functions

| [onFinalResult](on-final-result.html) | Is called after all extensions's [transformFindings](transform-findings.html) were called.`open fun onFinalResult(result: `[`Detektion`](../-detektion/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onRawResult](on-raw-result.html) | Is called before any [transformFindings](transform-findings.html) calls were executed.`open fun onRawResult(result: `[`Detektion`](../-detektion/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [transformFindings](transform-findings.html) | Allows to transform the reported findings e.g. apply custom filtering.`open fun transformFindings(findings: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`RuleSetId`](../-rule-set-id.html)`, `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>>): `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`RuleSetId`](../-rule-set-id.html)`, `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>>` |

