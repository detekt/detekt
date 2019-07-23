---
title: Rule.includes - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Rule](index.html) / [includes](./includes.html)

# includes

`open val includes: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`PathMatcher`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html)`>?`

When specified this rule only runs on KtFile's with paths matching any inclusion pattern.

**Return**
path matchers or null which means for every KtFile this rule must run

