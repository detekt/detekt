---
title: FileProcessListener.onFinish - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [FileProcessListener](index.html) / [onFinish](./on-finish.html)

# onFinish

`open fun ~~onFinish~~(files: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<KtFile>, result: `[`Detektion`](../-detektion/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)
**Deprecated:** Use alternative with a binding context.


`open fun onFinish(files: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<KtFile>, result: `[`Detektion`](../-detektion/index.html)`, bindingContext: BindingContext): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Mainly use this method to save computed metrics from KtFile's to the {@link Detektion} container.
Do not do heavy computations here as this method is called from the main thread.

This method is called before any [ReportingExtension](../-reporting-extension/index.html).

