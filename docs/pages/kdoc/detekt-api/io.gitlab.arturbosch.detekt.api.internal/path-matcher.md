---
title: pathMatcher - detekt-api
---

[detekt-api](../index.html) / [io.gitlab.arturbosch.detekt.api.internal](index.html) / [pathMatcher](./path-matcher.html)

# pathMatcher

`fun pathMatcher(pattern: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`PathMatcher`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html)

Converts given [pattern](path-matcher.html#io.gitlab.arturbosch.detekt.api.internal$pathMatcher(kotlin.String)/pattern) into a [PathMatcher](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html) specified by [FileSystem.getPathMatcher](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)).
We only support the "glob:" syntax to stay os independently.
Internally a globbing pattern is transformed to a regex respecting the Windows file system.

