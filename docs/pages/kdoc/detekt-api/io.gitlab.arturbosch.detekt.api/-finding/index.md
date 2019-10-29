---
title: Finding - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Finding](./index.html)

# Finding

`interface Finding : `[`Compactable`](../-compactable/index.html)`, `[`HasEntity`](../-has-entity/index.html)`, `[`HasMetrics`](../-has-metrics/index.html)

Base interface of detection findings. Inherits a bunch of useful behaviour
from sub interfaces.

Basic behaviour of a finding is that is can be assigned to an id and a source code position described as
an entity. Metrics and entity references can also considered for deeper characterization.

### Properties

| [id](id.html) | `abstract val id: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [issue](issue.html) | `abstract val issue: `[`Issue`](../-issue/index.html) |
| [message](message.html) | `abstract val message: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [references](references.html) | `abstract val references: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Entity`](../-entity/index.html)`>` |

### Functions

| [messageOrDescription](message-or-description.html) | Explanation why this finding was raised.`abstract fun messageOrDescription(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inheritors

| [CodeSmell](../-code-smell/index.html) | A code smell indicates any possible design problem inside a program's source code. The type of a code smell is described by an [Issue](../-issue/index.html).`open class CodeSmell : `[`Finding`](./index.html) |

