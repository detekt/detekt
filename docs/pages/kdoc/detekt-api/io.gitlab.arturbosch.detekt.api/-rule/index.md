---
title: Rule - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Rule](./index.html)

# Rule

`abstract class Rule : `[`BaseRule`](../-base-rule/index.html)`, `[`ConfigAware`](../-config-aware/index.html)

A rule defines how one specific code structure should look like. If code is found
which does not meet this structure, it is considered as harmful regarding maintainability
or readability.

A rule is implemented using the visitor pattern and should be started using the visit(KtFile)
function. If calculations must be done before or after the visiting process, here are
two predefined (preVisit/postVisit) functions which can be overridden to setup/teardown additional data.

**Author**
Artur Bosch

**Author**
Marvin Ramin

### Constructors

| [&lt;init&gt;](-init-.html) | `Rule(ruleSetConfig: `[`Config`](../-config/index.html)` = Config.empty, ruleContext: `[`Context`](../-context/index.html)` = DefaultContext())`<br>A rule defines how one specific code structure should look like. If code is found which does not meet this structure, it is considered as harmful regarding maintainability or readability. |

### Properties

| [aliases](aliases.html) | `val aliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>`<br>List of rule ids which can optionally be used in suppress annotations to refer to this rule. |
| [defaultRuleIdAliases](default-rule-id-aliases.html) | `open val defaultRuleIdAliases: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>`<br>The default names which can be used instead of this #ruleId to refer to this rule in suppression's. |
| [excludes](excludes.html) | `open val excludes: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`PathMatcher`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html)`>?`<br>When specified this rule will not run on KtFile's having a path matching any exclusion pattern. |
| [includes](includes.html) | `open val includes: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`PathMatcher`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html)`>?`<br>When specified this rule only runs on KtFile's with paths matching any inclusion pattern. |
| [issue](issue.html) | `abstract val issue: `[`Issue`](../-issue/index.html)<br>A rule is motivated to point out a specific issue in the code base. |
| [ruleId](rule-id.html) | `val ruleId: `[`RuleId`](../-rule-id.html)<br>An id this rule is identified with. Conventionally the rule id is derived from the issue id as these two classes have a coexistence. |
| [ruleSetConfig](rule-set-config.html) | `open val ruleSetConfig: `[`Config`](../-config/index.html)<br>Wrapped configuration of the ruleSet this rule is in. Use #valueOrDefault function to retrieve properties specified for the rule implementing this interface instead. Only use this property directly if you need a specific rule set property. |

### Inherited Properties

| [active](../-config-aware/active.html) | `open val active: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is this rule specified as active in configuration? If an rule is not specified in the underlying configuration, we assume it should not be run. |
| [autoCorrect](../-config-aware/auto-correct.html) | `open val autoCorrect: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Does this rule have auto correct specified in configuration? For auto correction to work the rule set itself enable it. |
| [bindingContext](../-base-rule/binding-context.html) | `var bindingContext: BindingContext` |
| [context](../-base-rule/context.html) | `val context: `[`Context`](../-context/index.html) |

### Functions

| [report](report.html) | `fun report(finding: `[`Finding`](../-finding/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`fun report(findings: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Simplified version of [Context.report](../-context/report.html) with aliases retrieval from the config. |
| [visitCondition](visit-condition.html) | `open fun visitCondition(root: KtFile): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Basic mechanism to decide if a rule should run or not. |

### Inherited Functions

| [postVisit](../-base-rule/post-visit.html) | `open fun postVisit(root: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Could be overridden by subclasses to specify a behaviour which should be done after visiting kotlin elements. |
| [preVisit](../-base-rule/pre-visit.html) | `open fun preVisit(root: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Could be overridden by subclasses to specify a behaviour which should be done before visiting kotlin elements. |
| [subConfig](../-config-aware/sub-config.html) | `open fun subConfig(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Config`](../-config/index.html)<br>Tries to retrieve part of the configuration based on given key. |
| [valueOrDefault](../-config-aware/value-or-default.html) | `open fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrDefault(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: `[`T`](../-config-aware/value-or-default.html#T)`): `[`T`](../-config-aware/value-or-default.html#T)<br>Retrieves a sub configuration or value based on given key. If configuration property cannot be found the specified default value is returned. |
| [valueOrNull](../-config-aware/value-or-null.html) | `open fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrNull(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`T`](../-config-aware/value-or-null.html#T)`?`<br>Retrieves a sub configuration or value based on given key. If the configuration property cannot be found, null is returned. |
| [visit](../-base-rule/visit.html) | `open fun visit(root: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitFile](../-base-rule/visit-file.html) | `fun visitFile(root: KtFile, bindingContext: BindingContext = BindingContext.EMPTY): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Before starting visiting kotlin elements, a check is performed if this rule should be triggered. Pre- and post-visit-hooks are executed before/after the visiting process. BindingContext holds the result of the semantic analysis of the source code by the Kotlin compiler. Rules that rely on symbols and types being resolved can use the BindingContext for this analysis. Note that detekt must receive the correct compile classpath for the code being analyzed otherwise the default value BindingContext.EMPTY will be used and it will not be possible for detekt to resolve types or symbols. |
| [withAutoCorrect](../-config-aware/with-auto-correct.html) | `open fun withAutoCorrect(block: () -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>If your rule supports to automatically correct the misbehaviour of underlying smell, specify your code inside this method call, to allow the user of your rule to trigger auto correction only when needed. |

### Inheritors

| [ThresholdRule](../-threshold-rule/index.html) | `abstract class ThresholdRule : `[`Rule`](./index.html)<br>Provides a threshold attribute for this rule, which is specified manually for default values but can be also obtained from within a configuration object. |

