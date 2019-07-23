---
title: SplitPattern.<init> - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [SplitPattern](index.html) / [&lt;init&gt;](./-init-.html)

# &lt;init&gt;

`SplitPattern(text: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, delimiters: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = ",", removeTrailingAsterisks: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = true)`

Splits given text into parts and provides testing utilities for its elements.
Basic use cases are to specify different function or class names in the detekt
yaml config and test for their appearance in specific rules.

