---
title: RuleSet - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [RuleSet](./index.html)

# RuleSet

`class RuleSet`

A rule set is a collection of rules and must be defined within a rule set provider implementation.

### Constructors

| [&lt;init&gt;](-init-.html) | A rule set is a collection of rules and must be defined within a rule set provider implementation.`RuleSet(id: `[`RuleSetId`](../-rule-set-id.html)`, rules: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BaseRule`](../../io.gitlab.arturbosch.detekt.api.internal/-base-rule/index.html)`>)` |

### Properties

| [id](id.html) | `val id: `[`RuleSetId`](../-rule-set-id.html) |
| [pathFilters](path-filters.html) | Is used to determine if a given [KtFile](#) should be analyzed at all.`var ~~pathFilters~~: `[`PathFilters`](../../io.gitlab.arturbosch.detekt.api.internal/-path-filters/index.html)`?` |
| [rules](rules.html) | `val rules: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`BaseRule`](../../io.gitlab.arturbosch.detekt.api.internal/-base-rule/index.html)`>` |

### Functions

| [accept](accept.html) | Visits given file with all rules of this rule set, returning a list of all code smell findings.`fun ~~accept~~(file: KtFile, bindingContext: BindingContext = BindingContext.EMPTY): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>` |

