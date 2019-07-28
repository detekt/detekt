---
title: Location.from - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Location](index.html) / [from](./from.html)

# from

`fun from(element: PsiElement, offset: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0): `[`Location`](index.html)

Creates a [Location](index.html) from a [PsiElement](#).
If the element can't be determined, the [KtFile](#) with a character offset can be used.

