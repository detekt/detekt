---
title: Config.hierarchicalSequence - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Config](index.html) / [hierarchicalSequence](./hierarchical-sequence.html)

# hierarchicalSequence

`open val hierarchicalSequence: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?`

Keeps track of which key was taken to [subConfig](sub-config.html) this configuration.
Sub-sequential calls to [subConfig](sub-config.html) are tracked by '&gt;' as a separator.

May be null if this is the top most configuration object.

