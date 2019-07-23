---
title: Detektion - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Detektion](./index.html)

# Detektion

`interface Detektion`

Storage for all kinds of findings and additional information
which needs to be transferred from the detekt engine to the user.

**Author**
Artur Bosch

### Properties

| [findings](findings.html) | `abstract val findings: `[`Map`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)`<`[`RuleSetId`](../-rule-set-id.html)`, `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Finding`](../-finding/index.html)`>>` |
| [metrics](metrics.html) | `abstract val metrics: `[`Collection`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)`<`[`ProjectMetric`](../-project-metric/index.html)`>` |
| [notifications](notifications.html) | `abstract val notifications: `[`Collection`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)`<`[`Notification`](../-notification/index.html)`>` |

### Functions

| [add](add.html) | `abstract fun add(notification: `[`Notification`](../-notification/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`abstract fun add(projectMetric: `[`ProjectMetric`](../-project-metric/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [addData](add-data.html) | `abstract fun <V> addData(key: Key<`[`V`](add-data.html#V)`>, value: `[`V`](add-data.html#V)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [getData](get-data.html) | `abstract fun <V> getData(key: Key<`[`V`](get-data.html#V)`>): `[`V`](get-data.html#V)`?` |

