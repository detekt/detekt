---
title: CodeSmell.<init> - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [CodeSmell](index.html) / [&lt;init&gt;](./-init-.html)

# &lt;init&gt;

`CodeSmell(issue: `[`Issue`](../-issue/index.html)`, entity: `[`Entity`](../-entity/index.html)`, message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, metrics: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Metric`](../-metric/index.html)`> = listOf(), references: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Entity`](../-entity/index.html)`> = listOf())`

A code smell indicates any possible design problem inside a program's source code.
The type of a code smell is described by an [Issue](../-issue/index.html).

If the design problem results from metric violations, a list of [Metric](../-metric/index.html)'s
can describe further the kind of metrics.

If the design problem manifests by different source locations, references to these
locations can be stored in additional [Entity](../-entity/index.html)'s.

