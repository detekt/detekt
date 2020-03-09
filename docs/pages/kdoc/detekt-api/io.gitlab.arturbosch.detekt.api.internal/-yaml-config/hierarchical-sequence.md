---
title: YamlConfig.hierarchicalSequence - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api.internal](../index.html) / [YamlConfig](index.html) / [hierarchicalSequence](./hierarchical-sequence.html)

# hierarchicalSequence

`val hierarchicalSequence: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?`

Keeps track of which key was taken to [subConfig](../../io.gitlab.arturbosch.detekt.api/-config/sub-config.html) this configuration.
Sub-sequential calls to [subConfig](../../io.gitlab.arturbosch.detekt.api/-config/sub-config.html) are tracked by '&gt;' as a separator.

May be null if this is the top most configuration object.

