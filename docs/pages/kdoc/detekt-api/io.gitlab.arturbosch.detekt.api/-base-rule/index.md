---
title: BaseRule - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [BaseRule](./index.html)

# BaseRule

`abstract class BaseRule : `[`DetektVisitor`](../-detekt-visitor/index.html)`, `[`Context`](../-context/index.html)

Defines the visiting mechanism for KtFile's.

Custom rule implementations should actually use [Rule](../-rule/index.html) as base class.

The extraction of this class from [Rule](../-rule/index.html) actually resulted from the need
of running many different checks on the same KtFile but within a single
potential costly visiting process, see [MultiRule](../-multi-rule/index.html).

This base rule class abstracts over single and multi rules and allows the
detekt core engine to only care about a single type.

### Constructors

| [&lt;init&gt;](-init-.html) | `BaseRule(context: `[`Context`](../-context/index.html)` = DefaultContext())`<br>Defines the visiting mechanism for KtFile's. |

### Properties

| [bindingContext](binding-context.html) | `var bindingContext: BindingContext` |
| [context](context.html) | `val context: `[`Context`](../-context/index.html) |
| [ruleId](rule-id.html) | `open val ruleId: `[`RuleId`](../-rule-id.html) |

### Functions

| [postVisit](post-visit.html) | `open fun postVisit(root: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Could be overridden by subclasses to specify a behaviour which should be done after visiting kotlin elements. |
| [preVisit](pre-visit.html) | `open fun preVisit(root: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Could be overridden by subclasses to specify a behaviour which should be done before visiting kotlin elements. |
| [visit](visit.html) | `open fun visit(root: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitCondition](visit-condition.html) | `abstract fun visitCondition(root: KtFile): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Basic mechanism to decide if a rule should run or not. |
| [visitFile](visit-file.html) | `fun visitFile(root: KtFile, bindingContext: BindingContext = BindingContext.EMPTY): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Before starting visiting kotlin elements, a check is performed if this rule should be triggered. Pre- and post-visit-hooks are executed before/after the visiting process. BindingContext holds the result of the semantic analysis of the source code by the Kotlin compiler. Rules that rely on symbols and types being resolved can use the BindingContext for this analysis. Note that detekt must receive the correct compile classpath for the code being analyzed otherwise the default value BindingContext.EMPTY will be used and it will not be possible for detekt to resolve types or symbols. |

### Inheritors

| [MultiRule](../-multi-rule/index.html) | `abstract class MultiRule : `[`BaseRule`](./index.html) |
| [Rule](../-rule/index.html) | `abstract class Rule : `[`BaseRule`](./index.html)`, `[`ConfigAware`](../-config-aware/index.html)<br>A rule defines how one specific code structure should look like. If code is found which does not meet this structure, it is considered as harmful regarding maintainability or readability. |

