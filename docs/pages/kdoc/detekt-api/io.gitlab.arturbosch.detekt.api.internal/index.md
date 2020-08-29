---
title: io.gitlab.arturbosch.detekt.api.internal - detekt-api
---

[detekt-api](../index.html) / [io.gitlab.arturbosch.detekt.api.internal](./index.html)

## Package io.gitlab.arturbosch.detekt.api.internal

### Types

| [BaseConfig](-base-config/index.html) | Convenient base configuration which parses/casts the configuration value based on the type of the default value.`abstract class BaseConfig : `[`Config`](../io.gitlab.arturbosch.detekt.api/-config/index.html) |
| [BaseRule](-base-rule/index.html) | Defines the visiting mechanism for KtFile's.`abstract class BaseRule : `[`DetektVisitor`](../io.gitlab.arturbosch.detekt.api/-detekt-visitor/index.html)`, `[`Context`](../io.gitlab.arturbosch.detekt.api/-context/index.html) |
| [CommaSeparatedPattern](-comma-separated-pattern/index.html) | `class CommaSeparatedPattern : `[`SplitPattern`](../io.gitlab.arturbosch.detekt.api/-split-pattern/index.html) |
| [CompilerResources](-compiler-resources/index.html) | Provides compiler resources.`data class CompilerResources` |
| [CompositeConfig](-composite-config/index.html) | Wraps two different configuration which should be considered when retrieving properties.`class CompositeConfig : `[`Config`](../io.gitlab.arturbosch.detekt.api/-config/index.html)`, `[`ValidatableConfiguration`](-validatable-configuration/index.html) |
| [DefaultRuleSetProvider](-default-rule-set-provider.html) | Interface which marks sub-classes as provided by detekt via the rules sub-module.`interface DefaultRuleSetProvider : `[`RuleSetProvider`](../io.gitlab.arturbosch.detekt.api/-rule-set-provider/index.html) |
| [DisabledAutoCorrectConfig](-disabled-auto-correct-config/index.html) | `class DisabledAutoCorrectConfig : `[`Config`](../io.gitlab.arturbosch.detekt.api/-config/index.html)`, `[`ValidatableConfiguration`](-validatable-configuration/index.html) |
| [FailFastConfig](-fail-fast-config/index.html) | `data class FailFastConfig : `[`Config`](../io.gitlab.arturbosch.detekt.api/-config/index.html)`, `[`ValidatableConfiguration`](-validatable-configuration/index.html) |
| [PathFilters](-path-filters/index.html) | `class PathFilters` |
| [SimpleNotification](-simple-notification/index.html) | `data class SimpleNotification : `[`Notification`](../io.gitlab.arturbosch.detekt.api/-notification/index.html) |
| [ValidatableConfiguration](-validatable-configuration/index.html) | `interface ValidatableConfiguration` |
| [YamlConfig](-yaml-config/index.html) | Config implementation using the yaml format. SubConfigurations can return sub maps according to the yaml specification.`class YamlConfig : `[`BaseConfig`](-base-config/index.html)`, `[`ValidatableConfiguration`](-validatable-configuration/index.html) |

### Extensions for External Classes

| [org.jetbrains.kotlin.psi.KtAnnotated](org.jetbrains.kotlin.psi.-kt-annotated/index.html) |  |
| [org.jetbrains.kotlin.psi.KtElement](org.jetbrains.kotlin.psi.-kt-element/index.html) |  |

### Properties

| [DEFAULT_PROPERTY_EXCLUDES](-d-e-f-a-u-l-t_-p-r-o-p-e-r-t-y_-e-x-c-l-u-d-e-s.html) | Known existing properties on rule's which my be absent in the default-detekt-config.yml.`val DEFAULT_PROPERTY_EXCLUDES: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Functions

| [createPathFilters](create-path-filters.html) | `fun `[`Config`](../io.gitlab.arturbosch.detekt.api/-config/index.html)`.createPathFilters(): `[`PathFilters`](-path-filters/index.html)`?` |
| [pathMatcher](path-matcher.html) | Converts given [pattern](path-matcher.html#io.gitlab.arturbosch.detekt.api.internal$pathMatcher(kotlin.String)/pattern) into a [PathMatcher](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html) specified by [FileSystem.getPathMatcher](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)). We only support the "glob:" syntax to stay os independently. Internally a globbing pattern is transformed to a regex respecting the Windows file system.`fun pathMatcher(pattern: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`PathMatcher`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html) |
| [validateConfig](validate-config.html) | `fun validateConfig(config: `[`Config`](../io.gitlab.arturbosch.detekt.api/-config/index.html)`, baseline: `[`Config`](../io.gitlab.arturbosch.detekt.api/-config/index.html)`, excludePatterns: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`Regex`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)`> = CommaSeparatedPattern(DEFAULT_PROPERTY_EXCLUDES).mapToRegex()): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Notification`](../io.gitlab.arturbosch.detekt.api/-notification/index.html)`>` |
| [valueOrDefaultCommaSeparated](value-or-default-comma-separated.html) | `fun `[`Config`](../io.gitlab.arturbosch.detekt.api/-config/index.html)`.valueOrDefaultCommaSeparated(key: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, default: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [whichDetekt](which-detekt.html) | Returns the bundled detekt version.`fun whichDetekt(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?` |
| [whichJava](which-java.html) | Returns the version of the running JVM.`fun whichJava(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [whichOS](which-o-s.html) | Returns the name of the running OS.`fun whichOS(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

