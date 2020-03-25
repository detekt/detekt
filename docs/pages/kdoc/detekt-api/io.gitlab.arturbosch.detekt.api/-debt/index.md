---
title: Debt - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Debt](./index.html)

# Debt

`data class Debt`

Debt describes the estimated amount of work needed to fix a given issue.

### Constructors

| [&lt;init&gt;](-init-.html) | Debt describes the estimated amount of work needed to fix a given issue.`Debt(days: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0, hours: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0, mins: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0)` |

### Properties

| [days](days.html) | `val days: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [hours](hours.html) | `val hours: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [mins](mins.html) | `val mins: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

### Functions

| [plus](plus.html) | Adds the other debt to this debt. This recalculates the potential overflow resulting from the addition.`operator fun plus(other: `[`Debt`](./index.html)`): `[`Debt`](./index.html) |
| [toString](to-string.html) | `fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Companion Object Properties

| [FIVE_MINS](-f-i-v-e_-m-i-n-s.html) | `val FIVE_MINS: `[`Debt`](./index.html) |
| [TEN_MINS](-t-e-n_-m-i-n-s.html) | `val TEN_MINS: `[`Debt`](./index.html) |
| [TWENTY_MINS](-t-w-e-n-t-y_-m-i-n-s.html) | `val TWENTY_MINS: `[`Debt`](./index.html) |

