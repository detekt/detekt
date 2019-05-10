---
title: RuleSet - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [RuleSet](./index.html)

# RuleSet

`class RuleSet`

A rule set is a collection of rules and must be defined within a rule set provider implementation.

**Author**
Artur Bosch

### Constructors

| [&lt;init&gt;](-init-.html) | `RuleSet(id: `[`RuleSetId`](../-rule-set-id.html)`, rules: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BaseRule`](../-base-rule/index.html)`>)`<br>A rule set is a collection of rules and must be defined within a rule set provider implementation. |

### Properties

| [id](id.html) | `val id: `[`RuleSetId`](../-rule-set-id.html) |
| [rules](rules.html) | `val rules: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BaseRule`](../-base-rule/index.html)`>` |

### Functions

| [accept](accept.html) | `fun accept(file: KtFile, bindingContext: BindingContext = BindingContext.EMPTY): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>`<br>Visits given file with all rules of this rule set, returning a list of all code smell findings.`fun accept(file: KtFile, ruleFilters: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`RuleId`](../-rule-id.html)`>, bindingContext: BindingContext = BindingContext.EMPTY): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>`<br>Visits given file with all non-filtered rules of this rule set. If a rule is a [MultiRule](../-multi-rule/index.html) the filters are passed to it via a setter and later used to filter sub rules of the [MultiRule](../-multi-rule/index.html). |

