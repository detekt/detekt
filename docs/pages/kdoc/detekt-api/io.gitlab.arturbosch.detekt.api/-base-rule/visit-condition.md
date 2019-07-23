---
title: BaseRule.visitCondition - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [BaseRule](index.html) / [visitCondition](./visit-condition.html)

# visitCondition

`abstract fun visitCondition(root: KtFile): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Basic mechanism to decide if a rule should run or not.

By default any rule which is declared 'active' in the [Config](../-config/index.html)
or not suppressed by a [Suppress](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-suppress/index.html) annotation on file level should run.

