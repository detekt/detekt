---
title: RuleSet.accept - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [RuleSet](index.html) / [accept](./accept.html)

# accept

`fun accept(file: KtFile, bindingContext: BindingContext = BindingContext.EMPTY): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>`

Visits given file with all rules of this rule set, returning a list
of all code smell findings.

`fun accept(file: KtFile, ruleFilters: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`RuleId`](../-rule-id.html)`>, bindingContext: BindingContext = BindingContext.EMPTY): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>`

Visits given file with all non-filtered rules of this rule set.
If a rule is a [MultiRule](../-multi-rule/index.html) the filters are passed to it via a setter
and later used to filter sub rules of the [MultiRule](../-multi-rule/index.html).

A list of findings is returned for given KtFile

