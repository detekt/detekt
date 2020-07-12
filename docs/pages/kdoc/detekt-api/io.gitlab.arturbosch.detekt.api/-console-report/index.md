---
title: ConsoleReport - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [ConsoleReport](./index.html)

# ConsoleReport

`abstract class ConsoleReport : `[`Extension`](../-extension/index.html)

Extension point which describes how findings should be printed on the console.

Additional [ConsoleReport](./index.html)'s can be made available through the [java.util.ServiceLoader](https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html) pattern.
If the default reporting mechanism should be turned off, exclude the entry 'FindingsReport'
in the 'console-reports' property of a detekt yaml config.

### Constructors

| [&lt;init&gt;](-init-.html) | Extension point which describes how findings should be printed on the console.`ConsoleReport()` |

### Functions

| [print](print.html) | Prints the rendered report to the given printer if anything was rendered at all.`fun ~~print~~(printer: `[`PrintStream`](https://docs.oracle.com/javase/8/docs/api/java/io/PrintStream.html)`, detektion: `[`Detektion`](../-detektion/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [render](render.html) | Converts the given [detektion](render.html#io.gitlab.arturbosch.detekt.api.ConsoleReport$render(io.gitlab.arturbosch.detekt.api.Detektion)/detektion) into a string representation to present it to the client. The implementation specifies which parts of the report are important to the user.`abstract fun render(detektion: `[`Detektion`](../-detektion/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |

