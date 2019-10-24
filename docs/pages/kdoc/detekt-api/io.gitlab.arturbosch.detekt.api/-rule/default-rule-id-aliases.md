---
title: Rule.defaultRuleIdAliases - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Rule](index.html) / [defaultRuleIdAliases](./default-rule-id-aliases.html)

# defaultRuleIdAliases

`open val defaultRuleIdAliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>`

The default names which can be used instead of this #ruleId to refer to this rule in suppression's.

When overriding this property make sure to meet following structure for detekt-generator to pick
it up and generate documentation for aliases:

```
    override val defaultRuleIdAliases = setOf("Name1", "Name2")
```

