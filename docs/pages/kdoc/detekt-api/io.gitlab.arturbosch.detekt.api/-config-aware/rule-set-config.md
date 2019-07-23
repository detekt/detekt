---
title: ConfigAware.ruleSetConfig - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [ConfigAware](index.html) / [ruleSetConfig](./rule-set-config.html)

# ruleSetConfig

`abstract val ruleSetConfig: `[`Config`](../-config/index.html)

Wrapped configuration of the ruleSet this rule is in.
Use #valueOrDefault function to retrieve properties specified for the rule
implementing this interface instead.
Only use this property directly if you need a specific rule set property.

