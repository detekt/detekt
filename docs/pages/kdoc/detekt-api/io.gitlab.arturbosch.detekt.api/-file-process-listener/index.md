---
title: FileProcessListener - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [FileProcessListener](./index.html)

# FileProcessListener

`interface FileProcessListener : `[`Extension`](../-extension/index.html)

Gather additional metrics about the analyzed kotlin file.
Pay attention to the thread policy of each function!

A bindingContext != BindingContext.EMPTY is only available if Kotlin compiler settings are used.

### Functions

| [onFinish](on-finish.html) | Mainly use this method to save computed metrics from KtFile's to the {@link Detektion} container. Do not do heavy computations here as this method is called from the main thread.`open fun ~~onFinish~~(files: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<KtFile>, result: `[`Detektion`](../-detektion/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`open fun onFinish(files: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<KtFile>, result: `[`Detektion`](../-detektion/index.html)`, bindingContext: BindingContext): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onProcess](on-process.html) | Called when processing of a file begins. This method is called from a thread pool thread. Heavy computations allowed.`open fun ~~onProcess~~(file: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`open fun onProcess(file: KtFile, bindingContext: BindingContext): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onProcessComplete](on-process-complete.html) | Called when processing of a file completes. This method is called from a thread pool thread. Heavy computations allowed.`open fun ~~onProcessComplete~~(file: KtFile, findings: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`open fun onProcessComplete(file: KtFile, findings: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>>, bindingContext: BindingContext): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onStart](on-start.html) | Use this to gather some additional information for the real onProcess function. This calculation should be lightweight as this method is called from the main thread.`open fun ~~onStart~~(files: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<KtFile>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`open fun onStart(files: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<KtFile>, bindingContext: BindingContext): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

