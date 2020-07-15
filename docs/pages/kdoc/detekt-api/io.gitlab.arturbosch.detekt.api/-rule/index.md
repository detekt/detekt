---
title: Rule - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Rule](./index.html)

# Rule

`abstract class Rule : `[`BaseRule`](../../io.gitlab.arturbosch.detekt.api.internal/-base-rule/index.html)`, `[`ConfigAware`](../-config-aware/index.html)

A rule defines how one specific code structure should look like. If code is found
which does not meet this structure, it is considered as harmful regarding maintainability
or readability.

A rule is implemented using the visitor pattern and should be started using the visit(KtFile)
function. If calculations must be done before or after the visiting process, here are
two predefined (preVisit/postVisit) functions which can be overridden to setup/teardown additional data.

### Constructors

| [&lt;init&gt;](-init-.html) | A rule defines how one specific code structure should look like. If code is found which does not meet this structure, it is considered as harmful regarding maintainability or readability.`Rule(ruleSetConfig: `[`Config`](../-config/index.html)` = Config.empty, ruleContext: `[`Context`](../-context/index.html)` = DefaultContext())` |

### Properties

| [aliases](aliases.html) | List of rule ids which can optionally be used in suppress annotations to refer to this rule.`val aliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [defaultRuleIdAliases](default-rule-id-aliases.html) | The default names which can be used instead of this #ruleId to refer to this rule in suppression's.`open val defaultRuleIdAliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [filters](filters.html) | Rules are aware of the paths they should run on via configuration properties.`open val filters: `[`PathFilters`](../../io.gitlab.arturbosch.detekt.api.internal/-path-filters/index.html)`?` |
| [issue](issue.html) | A rule is motivated to point out a specific issue in the code base.`abstract val issue: `[`Issue`](../-issue/index.html) |
| [ruleId](rule-id.html) | An id this rule is identified with. Conventionally the rule id is derived from the issue id as these two classes have a coexistence.`val ruleId: `[`RuleId`](../-rule-id.html) |
| [ruleSetConfig](rule-set-config.html) | Wrapped configuration of the ruleSet this rule is in. Use #valueOrDefault function to retrieve properties specified for the rule implementing this interface instead. Only use this property directly if you need a specific rule set property.`open val ruleSetConfig: `[`Config`](../-config/index.html) |

### Functions

| [report](report.html) | Simplified version of [Context.report](../-context/report.html) with rule defaults.`fun report(finding: `[`Finding`](../-finding/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`fun report(findings: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitCondition](visit-condition.html) | Basic mechanism to decide if a rule should run or not.`open fun visitCondition(root: KtFile): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Extension Functions

| [createPathFilters](../../io.gitlab.arturbosch.detekt.api.internal/create-path-filters.html) | `fun `[`Config`](../-config/index.html)`.createPathFilters(): `[`PathFilters`](../../io.gitlab.arturbosch.detekt.api.internal/-path-filters/index.html)`?` |
| [valueOrDefaultCommaSeparated](../../io.gitlab.arturbosch.detekt.api.internal/value-or-default-comma-separated.html) | `fun `[`Config`](../-config/index.html)`.valueOrDefaultCommaSeparated(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |

### Inheritors

| [ThresholdRule](../-threshold-rule/index.html) | Provides a threshold attribute for this rule, which is specified manually for default values but can be also obtained from within a configuration object.`abstract class ThresholdRule : `[`Rule`](./index.html) |

