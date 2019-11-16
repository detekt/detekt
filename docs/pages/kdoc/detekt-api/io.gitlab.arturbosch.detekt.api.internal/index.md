---
title: io.gitlab.arturbosch.detekt.api.internal - detekt-api
---

[detekt-api](../index.html) / [io.gitlab.arturbosch.detekt.api.internal](./index.html)

## Package io.gitlab.arturbosch.detekt.api.internal

### Types

| [CommaSeparatedPattern](-comma-separated-pattern/index.html) | `class CommaSeparatedPattern : `[`SplitPattern`](../io.gitlab.arturbosch.detekt.api/-split-pattern/index.html) |
| [CyclomaticComplexity](-cyclomatic-complexity/index.html) | Counts the cyclomatic complexity of nodes.`class CyclomaticComplexity : `[`DetektVisitor`](../io.gitlab.arturbosch.detekt.api/-detekt-visitor/index.html) |
| [DetektPomModel](-detekt-pom-model/index.html) | Adapted from https://github.com/pinterest/ktlint/blob/master/ktlint-core/src/main/kotlin/com/pinterest/ktlint/core/KtLint.kt Licenced under the MIT licence - https://github.com/pinterest/ktlint/blob/master/LICENSE`class DetektPomModel : UserDataHolderBase, PomModel` |
| [FailFastConfig](-fail-fast-config/index.html) | `data class FailFastConfig : `[`Config`](../io.gitlab.arturbosch.detekt.api/-config/index.html)`, `[`ValidatableConfiguration`](-validatable-configuration/index.html) |
| [PathFilters](-path-filters/index.html) | `class PathFilters` |
| [SimpleNotification](-simple-notification/index.html) | `data class SimpleNotification : `[`Notification`](../io.gitlab.arturbosch.detekt.api/-notification/index.html) |
| [ValidatableConfiguration](-validatable-configuration/index.html) | `interface ValidatableConfiguration` |

### Extensions for External Classes

| [org.jetbrains.kotlin.psi.KtAnnotated](org.jetbrains.kotlin.psi.-kt-annotated/index.html) |  |
| [org.jetbrains.kotlin.psi.KtElement](org.jetbrains.kotlin.psi.-kt-element/index.html) |  |
| [org.jetbrains.kotlin.psi.KtFile](org.jetbrains.kotlin.psi.-kt-file/index.html) |  |

### Properties

| [ABSOLUTE_PATH](-a-b-s-o-l-u-t-e_-p-a-t-h.html) | `val ABSOLUTE_PATH: Key<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [DEFAULT_PROPERTY_EXCLUDES](-d-e-f-a-u-l-t_-p-r-o-p-e-r-t-y_-e-x-c-l-u-d-e-s.html) | Known existing properties on rule's which my be absent in the default-detekt-config.yml.`const val DEFAULT_PROPERTY_EXCLUDES: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [RELATIVE_PATH](-r-e-l-a-t-i-v-e_-p-a-t-h.html) | `val RELATIVE_PATH: Key<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |

### Functions

| [createCompilerConfiguration](create-compiler-configuration.html) | Creates a compiler configuration for the kotlin compiler with all known sources and classpath jars. Be aware that if any path of [pathsToAnalyze](create-compiler-configuration.html#io.gitlab.arturbosch.detekt.api.internal$createCompilerConfiguration(kotlin.collections.List((java.nio.file.Path)), kotlin.collections.List((kotlin.String)), org.jetbrains.kotlin.config.LanguageVersion, org.jetbrains.kotlin.config.JvmTarget)/pathsToAnalyze) is a directory it is scanned for java and kotlin files.`fun createCompilerConfiguration(pathsToAnalyze: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Path`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html)`>, classpath: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>, languageVersion: LanguageVersion?, jvmTarget: JvmTarget): CompilerConfiguration` |
| [createKotlinCoreEnvironment](create-kotlin-core-environment.html) | Creates an environment instance which can be used to compile source code to KtFile's. This environment also allows to modify the resulting AST files.`fun createKotlinCoreEnvironment(configuration: CompilerConfiguration = CompilerConfiguration()): KotlinCoreEnvironment` |
| [pathMatcher](path-matcher.html) | Converts given [pattern](path-matcher.html#io.gitlab.arturbosch.detekt.api.internal$pathMatcher(kotlin.String)/pattern) into a [PathMatcher](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html) specified by [FileSystem.getPathMatcher](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)). We only support the "glob:" syntax to stay os independently. Internally a globbing pattern is transformed to a regex respecting the Windows file system.`fun pathMatcher(pattern: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`PathMatcher`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html) |
| [validateConfig](validate-config.html) | `fun validateConfig(config: `[`Config`](../io.gitlab.arturbosch.detekt.api/-config/index.html)`, baseline: `[`Config`](../io.gitlab.arturbosch.detekt.api/-config/index.html)`, excludePatterns: `[`Set`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)`<`[`Regex`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/-regex/index.html)`> = CommaSeparatedPattern(DEFAULT_PROPERTY_EXCLUDES).mapToRegex()): `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Notification`](../io.gitlab.arturbosch.detekt.api/-notification/index.html)`>` |

