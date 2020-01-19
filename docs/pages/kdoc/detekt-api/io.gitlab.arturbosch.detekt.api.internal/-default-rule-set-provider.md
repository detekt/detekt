---
title: DefaultRuleSetProvider - detekt-api
---

[detekt-api](../index.html) / [io.gitlab.arturbosch.detekt.api.internal](index.html) / [DefaultRuleSetProvider](./-default-rule-set-provider.html)

# DefaultRuleSetProvider

`interface DefaultRuleSetProvider : `[`RuleSetProvider`](../io.gitlab.arturbosch.detekt.api/-rule-set-provider/index.html)

Interface which marks sub-classes as provided by detekt via the rules sub-module.

Allows to implement "--disable-default-rulesets" effectively without the need
to manage a list of rule set names.

