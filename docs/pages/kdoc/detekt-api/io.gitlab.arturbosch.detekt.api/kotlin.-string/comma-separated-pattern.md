---
title: commaSeparatedPattern - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [kotlin.String](index.html) / [commaSeparatedPattern](./comma-separated-pattern.html)

# commaSeparatedPattern

`fun `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`.commaSeparatedPattern(vararg delimiters: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = arrayOf(",")): `[`Sequence`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>`

Splits given String into a sequence of strings splited by the provided delimiters ("," by default).

It also trims the strings and removes the empty ones

