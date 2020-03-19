---
title: RuleSetProvider.buildRuleset - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [RuleSetProvider](index.html) / [buildRuleset](./build-ruleset.html)

# buildRuleset

`open fun ~~buildRuleset~~(config: `[`Config`](../-config/index.html)`): `[`RuleSet`](../-rule-set/index.html)`?`
**Deprecated:** Exposes detekt-core implementation details.

Can return a rule set if this specific rule set is not considered as ignore.

Api notice: As the rule set id is not known before creating the rule set instance,
we must first create the rule set and then check if it is active.

