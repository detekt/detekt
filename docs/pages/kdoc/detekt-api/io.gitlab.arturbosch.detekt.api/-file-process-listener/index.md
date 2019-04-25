---
title: FileProcessListener - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [FileProcessListener](./index.html)

# FileProcessListener

`interface FileProcessListener : `[`Extension`](../-extension/index.html)

Gather additional metrics about the analyzed kotlin file.
Pay attention to the thread policy of each function!

**Author**
Artur Bosch

### Inherited Properties

| [id](../-extension/id.html) | `open val id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Name of the extension. |
| [priority](../-extension/priority.html) | `open val priority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Is used to run extensions in a specific order. The higher the priority the sooner the extension will run in detekt's lifecycle. |

### Functions

| [onFinish](on-finish.html) | `open fun onFinish(files: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<KtFile>, result: `[`Detektion`](../-detektion/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Mainly use this method to save computed metrics from KtFile's to the {@link Detektion} container. Do not do heavy computations here as this method is called from the main thread. |
| [onProcess](on-process.html) | `open fun onProcess(file: KtFile): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Called when processing of a file begins. This method is called from a thread pool thread. Heavy computations allowed. |
| [onProcessComplete](on-process-complete.html) | `open fun onProcessComplete(file: KtFile, findings: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Called when processing of a file completes. This method is called from a thread pool thread. Heavy computations allowed. |
| [onStart](on-start.html) | `open fun onStart(files: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<KtFile>): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Use this to gather some additional information for the real onProcess function. This calculation should be lightweight as this method is called from the main thread. |

### Inherited Functions

| [init](../-extension/init.html) | `open fun init(config: `[`Config`](../-config/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Allows to read any or even user defined properties from the detekt yaml config to setup this extension. |

