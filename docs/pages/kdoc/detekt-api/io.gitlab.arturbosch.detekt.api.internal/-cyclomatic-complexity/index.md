---
title: CyclomaticComplexity - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api.internal](../index.html) / [CyclomaticComplexity](./index.html)

# CyclomaticComplexity

`class CyclomaticComplexity : `[`DetektVisitor`](../../io.gitlab.arturbosch.detekt.api/-detekt-visitor/index.html)

Counts the cyclomatic complexity of nodes.

### Types

| [Config](-config/index.html) | `data class Config` |

### Constructors

| [&lt;init&gt;](-init-.html) | Counts the cyclomatic complexity of nodes.`CyclomaticComplexity(config: Config)` |

### Properties

| [complexity](complexity.html) | `var complexity: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| [visitBinaryExpression](visit-binary-expression.html) | `fun visitBinaryExpression(expression: KtBinaryExpression): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitBreakExpression](visit-break-expression.html) | `fun visitBreakExpression(expression: KtBreakExpression): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitCallExpression](visit-call-expression.html) | `fun visitCallExpression(expression: KtCallExpression): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitContinueExpression](visit-continue-expression.html) | `fun visitContinueExpression(expression: KtContinueExpression): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitIfExpression](visit-if-expression.html) | `fun visitIfExpression(expression: KtIfExpression): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitLoopExpression](visit-loop-expression.html) | `fun visitLoopExpression(loopExpression: KtLoopExpression): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitNamedFunction](visit-named-function.html) | `fun visitNamedFunction(function: KtNamedFunction): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitTryExpression](visit-try-expression.html) | `fun visitTryExpression(expression: KtTryExpression): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitWhenExpression](visit-when-expression.html) | `fun visitWhenExpression(expression: KtWhenExpression): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| [CONDITIONALS](-c-o-n-d-i-t-i-o-n-a-l-s.html) | `val CONDITIONALS: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<KtSingleValueToken!>` |
| [DEFAULT_NESTING_FUNCTIONS](-d-e-f-a-u-l-t_-n-e-s-t-i-n-g_-f-u-n-c-t-i-o-n-s.html) | `val DEFAULT_NESTING_FUNCTIONS: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |

### Companion Object Functions

| [calculate](calculate.html) | `fun calculate(node: KtElement, configure: (Config.() -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)`)? = null): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

