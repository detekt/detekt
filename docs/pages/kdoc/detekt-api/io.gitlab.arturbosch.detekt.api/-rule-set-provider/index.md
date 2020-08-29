---
title: RuleSetProvider - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [RuleSetProvider](./index.html)

# RuleSetProvider

`interface RuleSetProvider`

A rule set provider, as the name states, is responsible for creating rule sets.

When writing own rule set providers make sure to register them according the ServiceLoader documentation.
http://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html

### Properties

| [ruleSetId](rule-set-id.html) | Every rule set must be pre-configured with an ID to validate if this rule set must be created for current analysis.`abstract val ruleSetId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| [instance](instance.html) | This function must be implemented to provide custom rule sets. Make sure to pass the configuration to each rule to allow rules to be self configurable.`abstract fun instance(config: `[`Config`](../-config/index.html)`): `[`RuleSet`](../-rule-set/index.html) |

### Inheritors

| [DefaultRuleSetProvider](../../io.gitlab.arturbosch.detekt.api.internal/-default-rule-set-provider.html) | Interface which marks sub-classes as provided by detekt via the rules sub-module.`interface DefaultRuleSetProvider : `[`RuleSetProvider`](./index.html) |

