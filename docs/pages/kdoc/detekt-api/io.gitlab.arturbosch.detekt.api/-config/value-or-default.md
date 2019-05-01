---
title: Config.valueOrDefault - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Config](index.html) / [valueOrDefault](./value-or-default.html)

# valueOrDefault

`abstract fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrDefault(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: `[`T`](value-or-default.html#T)`): `[`T`](value-or-default.html#T)

Retrieves a sub configuration or value based on given key. If configuration property cannot be found
the specified default value is returned.

