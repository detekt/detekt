---
title: BaseRule.visitCondition - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api.internal](../index.html) / [BaseRule](index.html) / [visitCondition](./visit-condition.html)

# visitCondition

`abstract fun visitCondition(root: KtFile): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Basic mechanism to decide if a rule should run or not.

By default any rule which is declared 'active' in the [Config](../../io.gitlab.arturbosch.detekt.api/-config/index.html)
or not suppressed by a [Suppress](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-suppress/index.html) annotation on file level should run.

