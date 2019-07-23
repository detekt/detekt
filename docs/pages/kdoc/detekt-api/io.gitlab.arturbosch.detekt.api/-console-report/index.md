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

**Author**
Artur Bosch

### Constructors

| [&lt;init&gt;](-init-.html) | `ConsoleReport()`<br>Extension point which describes how findings should be printed on the console. |

### Inherited Properties

| [id](../-extension/id.html) | `open val id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Name of the extension. |
| [priority](../-extension/priority.html) | `open val priority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Is used to run extensions in a specific order. The higher the priority the sooner the extension will run in detekt's lifecycle. |

### Functions

| [print](print.html) | `fun print(printer: `[`PrintStream`](https://docs.oracle.com/javase/8/docs/api/java/io/PrintStream.html)`, detektion: `[`Detektion`](../-detektion/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [render](render.html) | `abstract fun render(detektion: `[`Detektion`](../-detektion/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |

### Inherited Functions

| [init](../-extension/init.html) | `open fun init(config: `[`Config`](../-config/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Allows to read any or even user defined properties from the detekt yaml config to setup this extension. |

