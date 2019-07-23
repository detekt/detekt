---
title: Extension - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Extension](./index.html)

# Extension

`interface Extension`

Defines extension points in detekt.
Currently supported extensions are:

* [FileProcessListener](../-file-process-listener/index.html)
* [ConsoleReport](../-console-report/index.html)
* [OutputReport](../-output-report/index.html)

**Author**
Artur Bosch

### Properties

| [id](id.html) | `open val id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Name of the extension. |
| [priority](priority.html) | `open val priority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Is used to run extensions in a specific order. The higher the priority the sooner the extension will run in detekt's lifecycle. |

### Functions

| [init](init.html) | `open fun init(config: `[`Config`](../-config/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Allows to read any or even user defined properties from the detekt yaml config to setup this extension. |

### Inheritors

| [ConsoleReport](../-console-report/index.html) | `abstract class ConsoleReport : `[`Extension`](./index.html)<br>Extension point which describes how findings should be printed on the console. |
| [FileProcessListener](../-file-process-listener/index.html) | `interface FileProcessListener : `[`Extension`](./index.html)<br>Gather additional metrics about the analyzed kotlin file. Pay attention to the thread policy of each function! |
| [OutputReport](../-output-report/index.html) | `abstract class OutputReport : `[`Extension`](./index.html)<br>Translates detekt's result container - [Detektion](../-detektion/index.html) - into an output report which is written inside a file. |

