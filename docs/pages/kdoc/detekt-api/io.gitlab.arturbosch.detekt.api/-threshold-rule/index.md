---
title: ThresholdRule - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [ThresholdRule](./index.html)

# ThresholdRule

`abstract class ThresholdRule : `[`Rule`](../-rule/index.html)

Provides a threshold attribute for this rule, which is specified manually for default values
but can be also obtained from within a configuration object.

### Constructors

| [&lt;init&gt;](-init-.html) | Provides a threshold attribute for this rule, which is specified manually for default values but can be also obtained from within a configuration object.`ThresholdRule(config: `[`Config`](../-config/index.html)`, defaultThreshold: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`)` |

### Properties

| [threshold](threshold.html) | The used threshold for this rule is loaded from the configuration or used from the constructor value.`val threshold: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

