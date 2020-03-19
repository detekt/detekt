---
title: ConsoleReport.render - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [ConsoleReport](index.html) / [render](./render.html)

# render

`abstract fun render(detektion: `[`Detektion`](../-detektion/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?`

Converts the given [detektion](render.html#io.gitlab.arturbosch.detekt.api.ConsoleReport$render(io.gitlab.arturbosch.detekt.api.Detektion)/detektion) into a string representation
to present it to the client.
The implementation specifies which parts of the report are important to the user.

