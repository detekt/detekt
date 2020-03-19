---
title: simplePatternToRegex - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [kotlin.String](index.html) / [simplePatternToRegex](./simple-pattern-to-regex.html)

# simplePatternToRegex

`fun `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`.simplePatternToRegex(): `[`Regex`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)

Convert a simple pattern String to a Regex

The simple pattern is a subset of the shell pattern matching or
[glob](https://en.wikipedia.org/wiki/Glob_programming)

'*' matches any zero or more characters
'?' matches any one character

