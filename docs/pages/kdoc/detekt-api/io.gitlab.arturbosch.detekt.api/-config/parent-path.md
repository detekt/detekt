---
title: Config.parentPath - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Config](index.html) / [parentPath](./parent-path.html)

# parentPath

`open val parentPath: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?`

Keeps track of which key was taken to [subConfig](sub-config.html) this configuration.
Sub-sequential calls to [subConfig](sub-config.html) are tracked with '&gt;' as a separator.

May be null if this is the top most configuration object.

