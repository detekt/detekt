---
title: RuleSet.accept - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [RuleSet](index.html) / [accept](./accept.html)

# accept

`fun ~~accept~~(file: KtFile, bindingContext: BindingContext = BindingContext.EMPTY): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>`
**Deprecated:** Exposes detekt-core implementation details.

Visits given file with all rules of this rule set, returning a list
of all code smell findings.

