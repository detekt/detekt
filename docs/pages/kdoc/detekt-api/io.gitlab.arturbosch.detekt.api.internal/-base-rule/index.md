---
title: BaseRule - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api.internal](../index.html) / [BaseRule](./index.html)

# BaseRule

`abstract class BaseRule : `[`DetektVisitor`](../../io.gitlab.arturbosch.detekt.api/-detekt-visitor/index.html)`, `[`Context`](../../io.gitlab.arturbosch.detekt.api/-context/index.html)

Defines the visiting mechanism for KtFile's.

Custom rule implementations should actually use [Rule](../../io.gitlab.arturbosch.detekt.api/-rule/index.html) as base class.

The extraction of this class from [Rule](../../io.gitlab.arturbosch.detekt.api/-rule/index.html) actually resulted from the need
of running many different checks on the same KtFile but within a single
potential costly visiting process, see [MultiRule](../../io.gitlab.arturbosch.detekt.api/-multi-rule/index.html).

This base rule class abstracts over single and multi rules and allows the
detekt core engine to only care about a single type.

### Constructors

| [&lt;init&gt;](-init-.html) | Defines the visiting mechanism for KtFile's.`BaseRule(context: `[`Context`](../../io.gitlab.arturbosch.detekt.api/-context/index.html)` = DefaultContext())` |

### Properties

| [bindingContext](binding-context.html) | `var bindingContext: BindingContext` |
| [compilerResources](compiler-resources.html) | `var compilerResources: `[`CompilerResources`](../-compiler-resources/index.html)`?` |
| [context](context.html) | `val context: `[`Context`](../../io.gitlab.arturbosch.detekt.api/-context/index.html) |
| [ruleId](rule-id.html) | `open val ruleId: `[`RuleId`](../../io.gitlab.arturbosch.detekt.api/-rule-id.html) |

### Functions

| [postVisit](post-visit.html) | Could be overridden by subclasses to specify a behaviour which should be done after visiting kotlin elements.`open fun postVisit(root: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [preVisit](pre-visit.html) | Could be overridden by subclasses to specify a behaviour which should be done before visiting kotlin elements.`open fun preVisit(root: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visit](visit.html) | Init function to start visiting the [KtFile](#). Can be overridden to start a different visiting process.`open fun visit(root: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitCondition](visit-condition.html) | Basic mechanism to decide if a rule should run or not.`abstract fun visitCondition(root: KtFile): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [visitFile](visit-file.html) | Before starting visiting kotlin elements, a check is performed if this rule should be triggered. Pre- and post-visit-hooks are executed before/after the visiting process. BindingContext holds the result of the semantic analysis of the source code by the Kotlin compiler. Rules that rely on symbols and types being resolved can use the BindingContext for this analysis. Note that detekt must receive the correct compile classpath for the code being analyzed otherwise the default value BindingContext.EMPTY will be used and it will not be possible for detekt to resolve types or symbols.`fun visitFile(root: KtFile, bindingContext: BindingContext = BindingContext.EMPTY, compilerResources: `[`CompilerResources`](../-compiler-resources/index.html)`? = null): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| [MultiRule](../../io.gitlab.arturbosch.detekt.api/-multi-rule/index.html) | Composite rule which delegates work to child rules. Can be used to combine different rules which do similar work like scanning the source code line by line to increase performance.`abstract class MultiRule : `[`BaseRule`](./index.html) |
| [Rule](../../io.gitlab.arturbosch.detekt.api/-rule/index.html) | A rule defines how one specific code structure should look like. If code is found which does not meet this structure, it is considered as harmful regarding maintainability or readability.`abstract class Rule : `[`BaseRule`](./index.html)`, `[`ConfigAware`](../../io.gitlab.arturbosch.detekt.api/-config-aware/index.html) |

