---
title: YamlConfig.load - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api.internal](../index.html) / [YamlConfig](index.html) / [load](./load.html)

# load

`fun load(path: `[`Path`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html)`): `[`Config`](../../io.gitlab.arturbosch.detekt.api/-config/index.html)

Factory method to load a yaml configuration. Given path must exist
and point to a readable file.

`fun load(reader: `[`Reader`](https://docs.oracle.com/javase/8/docs/api/java/io/Reader.html)`): `[`Config`](../../io.gitlab.arturbosch.detekt.api/-config/index.html)

Constructs a [YamlConfig](index.html) from any [Reader](https://docs.oracle.com/javase/8/docs/api/java/io/Reader.html).

Note the reader will be consumed and closed.

