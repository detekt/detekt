---
title: Entity.from - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Entity](index.html) / [from](./from.html)

# from

`fun from(element: PsiElement, offset: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0): `[`Entity`](index.html)

Factory function which retrieves all needed information from the PsiElement itself.

`fun from(element: PsiElement, location: `[`Location`](../-location/index.html)`): `[`Entity`](index.html)

Use this factory method if the location can be calculated much more precisely than
using the given PsiElement.

