---
title: MultiRule - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [MultiRule](./index.html)

# MultiRule

`abstract class MultiRule : `[`BaseRule`](../../io.gitlab.arturbosch.detekt.api.internal/-base-rule/index.html)

Composite rule which delegates work to child rules.
Can be used to combine different rules which do similar work like
scanning the source code line by line to increase performance.

### Constructors

| [&lt;init&gt;](-init-.html) | Composite rule which delegates work to child rules. Can be used to combine different rules which do similar work like scanning the source code line by line to increase performance.`MultiRule()` |

### Properties

| [activeRules](active-rules.html) | `var activeRules: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`Rule`](../-rule/index.html)`>` |
| [rules](rules.html) | `abstract val rules: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Rule`](../-rule/index.html)`>` |

### Functions

| [postVisit](post-visit.html) | Could be overridden by subclasses to specify a behaviour which should be done after visiting kotlin elements.`open fun postVisit(root: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [preVisit](pre-visit.html) | Could be overridden by subclasses to specify a behaviour which should be done before visiting kotlin elements.`open fun preVisit(root: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [runIfActive](run-if-active.html) | Preferred way to run child rules because this composite rule takes care of evaluating if a specific child should be run at all.`fun <T : `[`Rule`](../-rule/index.html)`> T.runIfActive(block: T.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitCondition](visit-condition.html) | Basic mechanism to decide if a rule should run or not.`open fun visitCondition(root: KtFile): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

