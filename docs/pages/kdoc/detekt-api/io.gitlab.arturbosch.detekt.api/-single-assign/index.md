---
title: SingleAssign - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [SingleAssign](./index.html)

# SingleAssign

`class SingleAssign<T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>`

Allows to assign a property just once.
Further assignments result in [IllegalStateException](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-illegal-state-exception/index.html)'s.

### Constructors

| [&lt;init&gt;](-init-.html) | Allows to assign a property just once. Further assignments result in [IllegalStateException](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-illegal-state-exception/index.html)'s.`SingleAssign()` |

### Functions

| [getValue](get-value.html) | Returns the [_value](#) if it was set before. Else an error is thrown.`operator fun getValue(thisRef: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?, property: `[`KProperty`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)`<*>): T` |
| [setValue](set-value.html) | Sets [_value](#) to the given [value](set-value.html#io.gitlab.arturbosch.detekt.api.SingleAssign$setValue(kotlin.Any, kotlin.reflect.KProperty((kotlin.Any)), io.gitlab.arturbosch.detekt.api.SingleAssign.T)/value). If it was set before, an error is thrown.`operator fun setValue(thisRef: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?, property: `[`KProperty`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-property/index.html)`<*>, value: T): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

