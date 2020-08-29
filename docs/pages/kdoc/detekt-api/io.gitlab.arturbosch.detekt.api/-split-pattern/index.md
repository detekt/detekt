---
title: SplitPattern - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [SplitPattern](./index.html)

# SplitPattern

`open class SplitPattern`

Splits given text into parts and provides testing utilities for its elements.
Basic use cases are to specify different function or class names in the detekt
yaml config and test for their appearance in specific rules.

### Constructors

| [&lt;init&gt;](-init-.html) | Splits given text into parts and provides testing utilities for its elements. Basic use cases are to specify different function or class names in the detekt yaml config and test for their appearance in specific rules.`SplitPattern(text: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, delimiters: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = ",", removeTrailingAsterisks: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = true)` |

### Functions

| [any](any.html) | Is there any element which matches the given [value](any.html#io.gitlab.arturbosch.detekt.api.SplitPattern$any(kotlin.String)/value)?`fun any(value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [contains](contains.html) | Does any part contain given [value](contains.html#io.gitlab.arturbosch.detekt.api.SplitPattern$contains(kotlin.String)/value)?`fun contains(value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [mapAll](map-all.html) | Transforms all parts by given [transform](map-all.html#io.gitlab.arturbosch.detekt.api.SplitPattern$mapAll(kotlin.Function1((kotlin.String, io.gitlab.arturbosch.detekt.api.SplitPattern.mapAll.T)))/transform) function.`fun <T> mapAll(transform: (`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`) -> T): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<T>` |
| [matches](matches.html) | Finds all parts which match the given [value](matches.html#io.gitlab.arturbosch.detekt.api.SplitPattern$matches(kotlin.String)/value).`fun matches(value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [none](none.html) | Tests if none of the parts contain the given [value](none.html#io.gitlab.arturbosch.detekt.api.SplitPattern$none(kotlin.String)/value).`fun none(value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [startWith](start-with.html) | Tests if any part starts with the given [value](start-with.html#io.gitlab.arturbosch.detekt.api.SplitPattern$startWith(kotlin.String)/value)`fun startWith(value: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Inheritors

| [CommaSeparatedPattern](../../io.gitlab.arturbosch.detekt.api.internal/-comma-separated-pattern/index.html) | `class CommaSeparatedPattern : `[`SplitPattern`](./index.html) |

