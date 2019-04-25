---
title: ThresholdRule - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [ThresholdRule](./index.html)

# ThresholdRule

`abstract class ThresholdRule : `[`Rule`](../-rule/index.html)

Provides a threshold attribute for this rule, which is specified manually for default values
but can be also obtained from within a configuration object.

### Constructors

| [&lt;init&gt;](-init-.html) | `ThresholdRule(config: `[`Config`](../-config/index.html)`, defaultThreshold: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`)`<br>Provides a threshold attribute for this rule, which is specified manually for default values but can be also obtained from within a configuration object. |

### Properties

| [threshold](threshold.html) | `val threshold: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>The used threshold for this rule is loaded from the configuration or used from the constructor value. |

### Inherited Properties

| [aliases](../-rule/aliases.html) | `val aliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>`<br>List of rule ids which can optionally be used in suppress annotations to refer to this rule. |
| [defaultRuleIdAliases](../-rule/default-rule-id-aliases.html) | `open val defaultRuleIdAliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>`<br>The default names which can be used instead of this #ruleId to refer to this rule in suppression's. |
| [excludes](../-rule/excludes.html) | `open val excludes: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`PathMatcher`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html)`>?`<br>When specified this rule will not run on KtFile's having a path matching any exclusion pattern. |
| [includes](../-rule/includes.html) | `open val includes: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`PathMatcher`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html)`>?`<br>When specified this rule only runs on KtFile's with paths matching any inclusion pattern. |
| [issue](../-rule/issue.html) | `abstract val issue: `[`Issue`](../-issue/index.html)<br>A rule is motivated to point out a specific issue in the code base. |
| [ruleId](../-rule/rule-id.html) | `val ruleId: `[`RuleId`](../-rule-id.html)<br>An id this rule is identified with. Conventionally the rule id is derived from the issue id as these two classes have a coexistence. |
| [ruleSetConfig](../-rule/rule-set-config.html) | `open val ruleSetConfig: `[`Config`](../-config/index.html)<br>Wrapped configuration of the ruleSet this rule is in. Use #valueOrDefault function to retrieve properties specified for the rule implementing this interface instead. Only use this property directly if you need a specific rule set property. |

### Inherited Functions

| [report](../-rule/report.html) | `fun report(finding: `[`Finding`](../-finding/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`fun report(findings: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Simplified version of [Context.report](../-context/report.html) with aliases retrieval from the config. |
| [visitCondition](../-rule/visit-condition.html) | `open fun visitCondition(root: KtFile): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Basic mechanism to decide if a rule should run or not. |

