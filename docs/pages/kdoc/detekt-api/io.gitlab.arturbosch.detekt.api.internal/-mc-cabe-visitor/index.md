---
title: McCabeVisitor - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api.internal](../index.html) / [McCabeVisitor](./index.html)

# McCabeVisitor

`class McCabeVisitor : `[`DetektVisitor`](../../io.gitlab.arturbosch.detekt.api/-detekt-visitor/index.html)

Counts the cyclomatic complexity of functions.

**Author**
Artur Bosch

**Author**
schalkms

**Author**
Sebastiano Poggi

### Constructors

| [&lt;init&gt;](-init-.html) | `McCabeVisitor(ignoreSimpleWhenEntries: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`)`<br>Counts the cyclomatic complexity of functions. |

### Properties

| [mcc](mcc.html) | `var mcc: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| [isInsideObjectLiteral](is-inside-object-literal.html) | `fun isInsideObjectLiteral(function: KtNamedFunction): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [visitCallExpression](visit-call-expression.html) | `fun visitCallExpression(expression: KtCallExpression): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitIfExpression](visit-if-expression.html) | `fun visitIfExpression(expression: KtIfExpression): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitLoopExpression](visit-loop-expression.html) | `fun visitLoopExpression(loopExpression: KtLoopExpression): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitNamedFunction](visit-named-function.html) | `fun visitNamedFunction(function: KtNamedFunction): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitTryExpression](visit-try-expression.html) | `fun visitTryExpression(expression: KtTryExpression): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [visitWhenExpression](visit-when-expression.html) | `fun visitWhenExpression(expression: KtWhenExpression): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

