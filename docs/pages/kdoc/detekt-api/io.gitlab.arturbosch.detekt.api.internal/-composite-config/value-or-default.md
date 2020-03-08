---
title: CompositeConfig.valueOrDefault - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api.internal](../index.html) / [CompositeConfig](index.html) / [valueOrDefault](./value-or-default.html)

# valueOrDefault

`fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrDefault(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: T): T`

Retrieves a sub configuration or value based on given key. If configuration property cannot be found
the specified default value is returned.

