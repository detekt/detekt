---
title: SplitPattern - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [SplitPattern](./index.html)

# SplitPattern

`class SplitPattern`

Splits given text into parts and provides testing utilities for its elements.
Basic use cases are to specify different function or class names in the detekt
yaml config and test for their appearance in specific rules.

### Constructors

| [&lt;init&gt;](-init-.html) | `SplitPattern(text: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, delimiters: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = ",", removeTrailingAsterisks: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = true)`<br>Splits given text into parts and provides testing utilities for its elements. Basic use cases are to specify different function or class names in the detekt yaml config and test for their appearance in specific rules. |

### Functions

| [any](any.html) | `fun any(value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is there any element which matches the given [value](any.html#io.gitlab.arturbosch.detekt.api.SplitPattern$any(kotlin.String)/value)? |
| [contains](contains.html) | `fun contains(value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Does any part contain given [value](contains.html#io.gitlab.arturbosch.detekt.api.SplitPattern$contains(kotlin.String)/value)? |
| [equals](equals.html) | `fun ~~equals~~(value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Is there any element which matches given [value](equals.html#io.gitlab.arturbosch.detekt.api.SplitPattern$equals(kotlin.String)/value)? |
| [mapAll](map-all.html) | `fun <T> mapAll(transform: (`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`) -> `[`T`](map-all.html#T)`): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`T`](map-all.html#T)`>`<br>Transforms all parts by given [transform](map-all.html#io.gitlab.arturbosch.detekt.api.SplitPattern$mapAll(kotlin.Function1((kotlin.String, io.gitlab.arturbosch.detekt.api.SplitPattern.mapAll.T)))/transform) function. |
| [matches](matches.html) | `fun matches(value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>`<br>Finds all parts which match the given [value](matches.html#io.gitlab.arturbosch.detekt.api.SplitPattern$matches(kotlin.String)/value). |
| [none](none.html) | `fun none(value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Tests if none of the parts contain the given [value](none.html#io.gitlab.arturbosch.detekt.api.SplitPattern$none(kotlin.String)/value). |
| [startWith](start-with.html) | `fun startWith(value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Tests if any part starts with the given [value](start-with.html#io.gitlab.arturbosch.detekt.api.SplitPattern$startWith(kotlin.String)/value) |

