---
title: MultiRule - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [MultiRule](./index.html)

# MultiRule

`abstract class MultiRule : `[`BaseRule`](../-base-rule/index.html)

### Constructors

| [&lt;init&gt;](-init-.html) | `MultiRule()` |

### Properties

| [activeRules](active-rules.html) | `var activeRules: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`Rule`](../-rule/index.html)`>` |
| [ruleFilters](rule-filters.html) | `var ruleFilters: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`RuleId`](../-rule-id.html)`>` |
| [rules](rules.html) | `abstract val rules: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Rule`](../-rule/index.html)`>` |

### Inherited Properties

| [bindingContext](../-base-rule/binding-context.html) | `var bindingContext: BindingContext` |
| [context](../-base-rule/context.html) | `val context: `[`Context`](../-context/index.html) |
| [ruleId](../-base-rule/rule-id.html) | `open val ruleId: `[`RuleId`](../-rule-id.html) |

### Functions

| [postVisit](post-visit.html) | `open fun postVisit(root: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Could be overridden by subclasses to specify a behaviour which should be done after visiting kotlin elements. |
| [preVisit](pre-visit.html) | `open fun preVisit(root: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Could be overridden by subclasses to specify a behaviour which should be done before visiting kotlin elements. |
| [runIfActive](run-if-active.html) | `fun <T : `[`Rule`](../-rule/index.html)`> `[`T`](run-if-active.html#T)`.runIfActive(block: `[`T`](run-if-active.html#T)`.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitCondition](visit-condition.html) | `open fun visitCondition(root: KtFile): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Basic mechanism to decide if a rule should run or not. |

### Inherited Functions

| [visit](../-base-rule/visit.html) | `open fun visit(root: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitFile](../-base-rule/visit-file.html) | `fun visitFile(root: KtFile, bindingContext: BindingContext = BindingContext.EMPTY): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Before starting visiting kotlin elements, a check is performed if this rule should be triggered. Pre- and post-visit-hooks are executed before/after the visiting process. BindingContext holds the result of the semantic analysis of the source code by the Kotlin compiler. Rules that rely on symbols and types being resolved can use the BindingContext for this analysis. Note that detekt must receive the correct compile classpath for the code being analyzed otherwise the default value BindingContext.EMPTY will be used and it will not be possible for detekt to resolve types or symbols. |

