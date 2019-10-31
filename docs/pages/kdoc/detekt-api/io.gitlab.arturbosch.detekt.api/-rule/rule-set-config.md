---
title: Rule.ruleSetConfig - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Rule](index.html) / [ruleSetConfig](./rule-set-config.html)

# ruleSetConfig

`open val ruleSetConfig: `[`Config`](../-config/index.html)

Wrapped configuration of the ruleSet this rule is in.
Use #valueOrDefault function to retrieve properties specified for the rule
implementing this interface instead.
Only use this property directly if you need a specific rule set property.

