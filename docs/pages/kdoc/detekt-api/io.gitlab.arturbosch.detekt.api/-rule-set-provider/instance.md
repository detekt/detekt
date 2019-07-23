---
title: RuleSetProvider.instance - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [RuleSetProvider](index.html) / [instance](./instance.html)

# instance

`abstract fun instance(config: `[`Config`](../-config/index.html)`): `[`RuleSet`](../-rule-set/index.html)

This function must be implemented to provide custom rule sets.
Make sure to pass the configuration to each rule to allow rules
to be self configurable.

