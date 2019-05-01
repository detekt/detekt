---
title: Rule.visitCondition - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Rule](index.html) / [visitCondition](./visit-condition.html)

# visitCondition

`open fun visitCondition(root: KtFile): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Overrides [BaseRule.visitCondition](../-base-rule/visit-condition.html)

Basic mechanism to decide if a rule should run or not.

By default any rule which is declared 'active' in the [Config](../-config/index.html)
or not suppressed by a [Suppress](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-suppress/index.html) annotation on file level should run.

