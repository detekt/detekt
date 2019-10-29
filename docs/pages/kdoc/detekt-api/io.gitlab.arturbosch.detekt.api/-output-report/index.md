---
title: OutputReport - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [OutputReport](./index.html)

# OutputReport

`abstract class OutputReport : `[`Extension`](../-extension/index.html)

Translates detekt's result container - [Detektion](../-detektion/index.html) - into an output report
which is written inside a file.

### Constructors

| [&lt;init&gt;](-init-.html) | Translates detekt's result container - [Detektion](../-detektion/index.html) - into an output report which is written inside a file.`OutputReport()` |

### Properties

| [ending](ending.html) | Supported ending of this report type.`abstract val ending: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [name](name.html) | Name of the report. Is used to exclude this report in the yaml config.`open val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |

### Functions

| [render](render.html) | Defines the translation process of detekt's result into a string.`abstract fun render(detektion: `[`Detektion`](../-detektion/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [write](write.html) | Renders result and writes it to the given [filePath](write.html#io.gitlab.arturbosch.detekt.api.OutputReport$write(java.nio.file.Path, io.gitlab.arturbosch.detekt.api.Detektion)/filePath).`fun write(filePath: `[`Path`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html)`, detektion: `[`Detektion`](../-detektion/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

