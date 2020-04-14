---
title: AnnotationExcluder.<init> - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [AnnotationExcluder](index.html) / [&lt;init&gt;](./-init-.html)

# &lt;init&gt;

`AnnotationExcluder(root: KtFile, excludes: `[`SplitPattern`](../-split-pattern/index.html)`)``AnnotationExcluder(root: KtFile, excludes: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>)`

Primary use case for an AnnotationExcluder is to decide if a KtElement should be
excluded from further analysis. This is done by checking if a special annotation
is present over the element.

