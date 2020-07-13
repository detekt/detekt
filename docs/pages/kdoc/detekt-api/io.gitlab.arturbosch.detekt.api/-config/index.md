---
title: Config - detekt-api
---

[detekt-api](../../index.html) / [io.gitlab.arturbosch.detekt.api](../index.html) / [Config](./index.html)

# Config

`interface Config`

A configuration holds information about how to configure specific rules.

### Exceptions

| [InvalidConfigurationError](-invalid-configuration-error/index.html) | Is thrown when loading a configuration results in errors.`class InvalidConfigurationError : `[`RuntimeException`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-runtime-exception/index.html) |

### Properties

| [parentPath](parent-path.html) | Keeps track of which key was taken to [subConfig](sub-config.html) this configuration. Sub-sequential calls to [subConfig](sub-config.html) are tracked with '&gt;' as a separator.`open val parentPath: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |

### Functions

| [subConfig](sub-config.html) | Tries to retrieve part of the configuration based on given key.`abstract fun subConfig(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Config`](./index.html) |
| [valueOrDefault](value-or-default.html) | Retrieves a sub configuration or value based on given key. If configuration property cannot be found the specified default value is returned.`open fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrDefault(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: T): T` |
| [valueOrNull](value-or-null.html) | Retrieves a sub configuration or value based on given key. If the configuration property cannot be found, null is returned.`abstract fun <T : `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`> valueOrNull(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): T?` |

### Companion Object Properties

| [ACTIVE_KEY](-a-c-t-i-v-e_-k-e-y.html) | `const val ACTIVE_KEY: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [CONFIG_SEPARATOR](-c-o-n-f-i-g_-s-e-p-a-r-a-t-o-r.html) | `const val CONFIG_SEPARATOR: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [empty](empty.html) | An empty configuration with no properties. This config should only be used in test cases. Always returns the default value except when 'active' is queried, it returns true .`val empty: `[`Config`](./index.html) |
| [EXCLUDES_KEY](-e-x-c-l-u-d-e-s_-k-e-y.html) | `const val EXCLUDES_KEY: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [INCLUDES_KEY](-i-n-c-l-u-d-e-s_-k-e-y.html) | `const val INCLUDES_KEY: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [PRIMITIVES](-p-r-i-m-i-t-i-v-e-s.html) | `val PRIMITIVES: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`KClass`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)`<out `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`>>` |

### Extension Functions

| [createPathFilters](../../io.gitlab.arturbosch.detekt.api.internal/create-path-filters.html) | `fun `[`Config`](./index.html)`.createPathFilters(): `[`PathFilters`](../../io.gitlab.arturbosch.detekt.api.internal/-path-filters/index.html)`?` |
| [valueOrDefaultCommaSeparated](../../io.gitlab.arturbosch.detekt.api.internal/value-or-default-comma-separated.html) | `fun `[`Config`](./index.html)`.valueOrDefaultCommaSeparated(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |

### Inheritors

| [BaseConfig](../../io.gitlab.arturbosch.detekt.api.internal/-base-config/index.html) | Convenient base configuration which parses/casts the configuration value based on the type of the default value.`abstract class BaseConfig : `[`Config`](./index.html) |
| [CompositeConfig](../../io.gitlab.arturbosch.detekt.api.internal/-composite-config/index.html) | Wraps two different configuration which should be considered when retrieving properties.`class CompositeConfig : `[`Config`](./index.html)`, `[`ValidatableConfiguration`](../../io.gitlab.arturbosch.detekt.api.internal/-validatable-configuration/index.html) |
| [ConfigAware](../-config-aware/index.html) | Interface which is implemented by each Rule class to provide utility functions to retrieve specific or generic properties from the underlying detekt configuration file.`interface ConfigAware : `[`Config`](./index.html) |
| [DisabledAutoCorrectConfig](../../io.gitlab.arturbosch.detekt.api.internal/-disabled-auto-correct-config/index.html) | `class DisabledAutoCorrectConfig : `[`Config`](./index.html)`, `[`ValidatableConfiguration`](../../io.gitlab.arturbosch.detekt.api.internal/-validatable-configuration/index.html) |
| [FailFastConfig](../../io.gitlab.arturbosch.detekt.api.internal/-fail-fast-config/index.html) | `data class FailFastConfig : `[`Config`](./index.html)`, `[`ValidatableConfiguration`](../../io.gitlab.arturbosch.detekt.api.internal/-validatable-configuration/index.html) |

