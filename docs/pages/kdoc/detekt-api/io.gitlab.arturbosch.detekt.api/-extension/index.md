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
* [ConfigValidator](../-config-validator/index.html)
* [ReportingExtension](../-reporting-extension/index.html)

### Properties

| [id](id.html) | Name of the extension.`open val id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [priority](priority.html) | Is used to run extensions in a specific order. The higher the priority the sooner the extension will run in detekt's lifecycle.`open val priority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| [init](init.html) | Allows to read any or even user defined properties from the detekt yaml config to setup this extension.`open fun init(config: `[`Config`](../-config/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Setup extension by querying common paths and config options.`open fun init(context: `[`SetupContext`](../-setup-context/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inheritors

| [ConfigValidator](../-config-validator/index.html) | An extension which allows users to validate parts of the configuration.`interface ConfigValidator : `[`Extension`](./index.html) |
| [ConsoleReport](../-console-report/index.html) | Extension point which describes how findings should be printed on the console.`abstract class ConsoleReport : `[`Extension`](./index.html) |
| [FileProcessListener](../-file-process-listener/index.html) | Gather additional metrics about the analyzed kotlin file. Pay attention to the thread policy of each function!`interface FileProcessListener : `[`Extension`](./index.html) |
| [OutputReport](../-output-report/index.html) | Translates detekt's result container - [Detektion](../-detektion/index.html) - into an output report which is written inside a file.`abstract class OutputReport : `[`Extension`](./index.html) |
| [ReportingExtension](../-reporting-extension/index.html) | Allows to intercept detekt's result container by listening to the initial and final state and manipulate the reported findings.`interface ReportingExtension : `[`Extension`](./index.html) |

