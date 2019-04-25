---
title: Rule.excludes - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Rule](index.html) / [excludes](./excludes.html)

# excludes

`open val excludes: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`PathMatcher`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html)`>?`

When specified this rule will not run on KtFile's having a path matching any exclusion pattern.

**Return**
path matchers or null which means no KtFile's get excluded except inclusion patterns are defined

