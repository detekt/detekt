---
title: FileProcessListener.onStart - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [FileProcessListener](index.html) / [onStart](./on-start.html)

# onStart

`open fun ~~onStart~~(files: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<KtFile>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)
**Deprecated:** Use alternative with a binding context.


`open fun onStart(files: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<KtFile>, bindingContext: BindingContext): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Use this to gather some additional information for the real onProcess function.
This calculation should be lightweight as this method is called from the main thread.

