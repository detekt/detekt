---
title: LazyRegex.<init> - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [LazyRegex](index.html) / [&lt;init&gt;](./-init-.html)

# &lt;init&gt;

`LazyRegex(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`

LazyRegex class provides a lazy evaluation of a Regex pattern for usages inside Rules.
It computes the value once when reaching the point of its usage and returns the same
value when requested again.

`key` &amp; `default` are used to retrieve a value from config.

