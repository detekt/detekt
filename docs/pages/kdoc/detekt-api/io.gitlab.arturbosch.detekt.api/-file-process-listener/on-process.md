---
title: FileProcessListener.onProcess - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [FileProcessListener](index.html) / [onProcess](./on-process.html)

# onProcess

`open fun ~~onProcess~~(file: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)
**Deprecated:** Use alternative with a binding context.


`open fun onProcess(file: KtFile, bindingContext: BindingContext): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Called when processing of a file begins.
This method is called from a thread pool thread. Heavy computations allowed.

