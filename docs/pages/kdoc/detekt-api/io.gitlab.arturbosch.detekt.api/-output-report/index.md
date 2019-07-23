---
title: OutputReport - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [OutputReport](./index.html)

# OutputReport

`abstract class OutputReport : `[`Extension`](../-extension/index.html)

Translates detekt's result container - [Detektion](../-detektion/index.html) - into an output report
which is written inside a file.

**Author**
Artur Bosch

**Author**
Marvin Ramin

### Constructors

| [&lt;init&gt;](-init-.html) | `OutputReport()`<br>Translates detekt's result container - [Detektion](../-detektion/index.html) - into an output report which is written inside a file. |

### Properties

| [ending](ending.html) | `abstract val ending: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Supported ending of this report type. |
| [name](name.html) | `open val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?`<br>Name of the report. Is used to exclude this report in the yaml config. |

### Inherited Properties

| [id](../-extension/id.html) | `open val id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Name of the extension. |
| [priority](../-extension/priority.html) | `open val priority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Is used to run extensions in a specific order. The higher the priority the sooner the extension will run in detekt's lifecycle. |

### Functions

| [render](render.html) | `abstract fun render(detektion: `[`Detektion`](../-detektion/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?`<br>Defines the translation process of detekt's result into a string. |
| [write](write.html) | `fun write(filePath: `[`Path`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html)`, detektion: `[`Detektion`](../-detektion/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Renders result and writes it to the given [filePath](write.html#io.gitlab.arturbosch.detekt.api.OutputReport$write(java.nio.file.Path, io.gitlab.arturbosch.detekt.api.Detektion)/filePath). |

### Inherited Functions

| [init](../-extension/init.html) | `open fun init(config: `[`Config`](../-config/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Allows to read any or even user defined properties from the detekt yaml config to setup this extension. |

