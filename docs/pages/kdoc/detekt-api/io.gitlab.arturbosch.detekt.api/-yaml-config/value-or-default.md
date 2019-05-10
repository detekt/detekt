---
title: YamlConfig.valueOrDefault - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [YamlConfig](index.html) / [valueOrDefault](./value-or-default.html)

# valueOrDefault

`fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrDefault(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: `[`T`](value-or-default.html#T)`): `[`T`](value-or-default.html#T)

Overrides [Config.valueOrDefault](../-config/value-or-default.html)

Retrieves a sub configuration or value based on given key. If configuration property cannot be found
the specified default value is returned.

