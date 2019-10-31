---
title: ConfigAware.valueOrDefault - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [ConfigAware](index.html) / [valueOrDefault](./value-or-default.html)

# valueOrDefault

`open fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrDefault(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: T): T`

Retrieves a sub configuration or value based on given key. If configuration property cannot be found
the specified default value is returned.

