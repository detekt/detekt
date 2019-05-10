---
title: ConfigAware - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [ConfigAware](./index.html)

# ConfigAware

`interface ConfigAware : `[`Config`](../-config/index.html)

Interface which is implemented by each Rule class to provide
utility functions to retrieve specific or generic properties
from the underlying detekt configuration file.

Be aware that there are three config levels by default:

* the top level config layer specifies rule sets and detekt engine properties
* the rule set level specifies properties concerning the whole rule set and rules
* the rule level provides additional properties which are used to configure rules

This interface operates on the rule set level as the rule set config is passed to each
rule in the #RuleSetProvider interface. This is due the fact that users create the
rule set and all rules upfront and letting them 'sub config' the rule set config would
be error-prone.

**Author**
Artur Bosch

### Properties

| [active](active.html) | `open val active: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is this rule specified as active in configuration? If an rule is not specified in the underlying configuration, we assume it should not be run. |
| [autoCorrect](auto-correct.html) | `open val autoCorrect: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Does this rule have auto correct specified in configuration? For auto correction to work the rule set itself enable it. |
| [ruleId](rule-id.html) | `abstract val ruleId: `[`RuleId`](../-rule-id.html)<br>Id which is used to retrieve the sub config for the rule implementing this interface. |
| [ruleSetConfig](rule-set-config.html) | `abstract val ruleSetConfig: `[`Config`](../-config/index.html)<br>Wrapped configuration of the ruleSet this rule is in. Use #valueOrDefault function to retrieve properties specified for the rule implementing this interface instead. Only use this property directly if you need a specific rule set property. |

### Functions

| [subConfig](sub-config.html) | `open fun subConfig(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Config`](../-config/index.html)<br>Tries to retrieve part of the configuration based on given key. |
| [valueOrDefault](value-or-default.html) | `open fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrDefault(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: `[`T`](value-or-default.html#T)`): `[`T`](value-or-default.html#T)<br>Retrieves a sub configuration or value based on given key. If configuration property cannot be found the specified default value is returned. |
| [valueOrNull](value-or-null.html) | `open fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrNull(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`T`](value-or-null.html#T)`?`<br>Retrieves a sub configuration or value based on given key. If the configuration property cannot be found, null is returned. |
| [withAutoCorrect](with-auto-correct.html) | `open fun withAutoCorrect(block: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>If your rule supports to automatically correct the misbehaviour of underlying smell, specify your code inside this method call, to allow the user of your rule to trigger auto correction only when needed. |

### Inheritors

| [Rule](../-rule/index.html) | `abstract class Rule : `[`BaseRule`](../-base-rule/index.html)`, `[`ConfigAware`](./index.html)<br>A rule defines how one specific code structure should look like. If code is found which does not meet this structure, it is considered as harmful regarding maintainability or readability. |

