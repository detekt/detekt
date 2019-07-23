---
title: RuleSetProvider - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [RuleSetProvider](./index.html)

# RuleSetProvider

`interface RuleSetProvider`

A rule set provider, as the name states, is responsible for creating rule sets.

When writing own rule set providers make sure to register them according the ServiceLoader documentation.
http://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html

**Author**
Artur Bosch

### Properties

| [ruleSetId](rule-set-id.html) | `abstract val ruleSetId: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Every rule set must be pre-configured with an ID to validate if this rule set must be created for current analysis. |

### Functions

| [buildRuleset](build-ruleset.html) | `open fun buildRuleset(config: `[`Config`](../-config/index.html)`): `[`RuleSet`](../-rule-set/index.html)`?`<br>Can return a rule set if this specific rule set is not considered as ignore. |
| [instance](instance.html) | `abstract fun instance(config: `[`Config`](../-config/index.html)`): `[`RuleSet`](../-rule-set/index.html)<br>This function must be implemented to provide custom rule sets. Make sure to pass the configuration to each rule to allow rules to be self configurable. |

