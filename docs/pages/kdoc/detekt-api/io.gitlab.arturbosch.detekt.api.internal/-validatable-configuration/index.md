---
title: ValidatableConfiguration - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api.internal](../index.html) / [ValidatableConfiguration](./index.html)

# ValidatableConfiguration

`interface ValidatableConfiguration`

### Functions

| [validate](validate.html) | `abstract fun validate(baseline: `[`Config`](../../io.gitlab.arturbosch.detekt.api/-config/index.html)`, excludePatterns: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`Regex`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)`>): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Notification`](../../io.gitlab.arturbosch.detekt.api/-notification/index.html)`>` |

### Inheritors

| [CompositeConfig](../-composite-config/index.html) | Wraps two different configuration which should be considered when retrieving properties.`class CompositeConfig : `[`Config`](../../io.gitlab.arturbosch.detekt.api/-config/index.html)`, `[`ValidatableConfiguration`](./index.html) |
| [DisabledAutoCorrectConfig](../-disabled-auto-correct-config/index.html) | `class DisabledAutoCorrectConfig : `[`Config`](../../io.gitlab.arturbosch.detekt.api/-config/index.html)`, `[`ValidatableConfiguration`](./index.html) |
| [FailFastConfig](../-fail-fast-config/index.html) | `data class FailFastConfig : `[`Config`](../../io.gitlab.arturbosch.detekt.api/-config/index.html)`, `[`ValidatableConfiguration`](./index.html) |
| [YamlConfig](../-yaml-config/index.html) | Config implementation using the yaml format. SubConfigurations can return sub maps according to the yaml specification.`class YamlConfig : `[`BaseConfig`](../-base-config/index.html)`, `[`ValidatableConfiguration`](./index.html) |

