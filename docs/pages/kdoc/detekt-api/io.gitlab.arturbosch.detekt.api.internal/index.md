---
title: io.gitlab.arturbosch.detekt.api.internal - detekt-api
---

[detekt-api](../index.html) / [io.gitlab.arturbosch.detekt.api.internal](./index.html)

## Package io.gitlab.arturbosch.detekt.api.internal

### Types

| [DetektPomModel](-detekt-pom-model/index.html) | `class DetektPomModel : UserDataHolderBase, PomModel`<br>Adapted from https://github.com/pinterest/ktlint/blob/master/ktlint-core/src/main/kotlin/com/pinterest/ktlint/core/KtLint.kt Licenced under the MIT licence - https://github.com/pinterest/ktlint/blob/master/LICENSE |
| [McCabeVisitor](-mc-cabe-visitor/index.html) | `class McCabeVisitor : `[`DetektVisitor`](../io.gitlab.arturbosch.detekt.api/-detekt-visitor/index.html)<br>Counts the cyclomatic complexity of functions. |
| [PathFilters](-path-filters/index.html) | `class PathFilters` |

### Extensions for External Classes

| [org.jetbrains.kotlin.psi.KtCallExpression](org.jetbrains.kotlin.psi.-kt-call-expression/index.html) |  |
| [org.jetbrains.kotlin.psi.KtFile](org.jetbrains.kotlin.psi.-kt-file/index.html) |  |

### Properties

| [ABSOLUTE_PATH](-a-b-s-o-l-u-t-e_-p-a-t-h.html) | `val ABSOLUTE_PATH: Key<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [RELATIVE_PATH](-r-e-l-a-t-i-v-e_-p-a-t-h.html) | `val RELATIVE_PATH: Key<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |

### Functions

| [createCompilerConfiguration](create-compiler-configuration.html) | `fun createCompilerConfiguration(pathsToAnalyze: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Path`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html)`>, classpath: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>, jvmTarget: JvmTarget): CompilerConfiguration`<br>Creates a compiler configuration for the kotlin compiler with all known sources and classpath jars. Be aware that if any path of [pathsToAnalyze](create-compiler-configuration.html#io.gitlab.arturbosch.detekt.api.internal$createCompilerConfiguration(kotlin.collections.List((java.nio.file.Path)), kotlin.collections.List((kotlin.String)), org.jetbrains.kotlin.config.JvmTarget)/pathsToAnalyze) is a directory it is scanned for java and kotlin files. |
| [createKotlinCoreEnvironment](create-kotlin-core-environment.html) | `fun createKotlinCoreEnvironment(configuration: CompilerConfiguration = CompilerConfiguration()): KotlinCoreEnvironment`<br>Creates an environment instance which can be used to compile source code to KtFile's. This environment also allows to modify the resulting AST files. |
| [pathMatcher](path-matcher.html) | `fun pathMatcher(pattern: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`PathMatcher`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html)<br>Converts given [pattern](path-matcher.html#io.gitlab.arturbosch.detekt.api.internal$pathMatcher(kotlin.String)/pattern) into a [PathMatcher](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html) specified by [FileSystem.getPathMatcher](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)). We only support the "glob:" syntax to stay os independently. Internally a globbing pattern is transformed to a regex respecting the Windows file system. |

