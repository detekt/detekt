---
title: DefaultContext.report - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [DefaultContext](index.html) / [report](./report.html)

# report

`open fun report(finding: `[`Finding`](../-finding/index.html)`, aliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>, ruleSetId: `[`RuleSetId`](../-rule-set-id.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Reports a single code smell finding.

Before adding a finding, it is checked if it is not suppressed
by @Suppress or @SuppressWarnings annotations.

`open fun report(findings: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>, aliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>, ruleSetId: `[`RuleSetId`](../-rule-set-id.html)`?): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Reports a list of code smell findings.

Before adding a finding, it is checked if it is not suppressed
by @Suppress or @SuppressWarnings annotations.

