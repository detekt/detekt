---
title: io.gitlab.arturbosch.detekt.api.internal - detekt-api
---

[detekt-api](../index.html) / [io.gitlab.arturbosch.detekt.api.internal](./index.html)

## Package io.gitlab.arturbosch.detekt.api.internal

### Types

| [McCabeVisitor](-mc-cabe-visitor/index.html) | `class McCabeVisitor : `[`DetektVisitor`](../io.gitlab.arturbosch.detekt.api/-detekt-visitor/index.html)<br>Counts the cyclomatic complexity of functions. |

### Extensions for External Classes

| [org.jetbrains.kotlin.psi.KtCallExpression](org.jetbrains.kotlin.psi.-kt-call-expression/index.html) |  |
| [org.jetbrains.kotlin.psi.KtFile](org.jetbrains.kotlin.psi.-kt-file/index.html) |  |

### Properties

| [ABSOLUTE_PATH](-a-b-s-o-l-u-t-e_-p-a-t-h.html) | `val ABSOLUTE_PATH: Key<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
| [RELATIVE_PATH](-r-e-l-a-t-i-v-e_-p-a-t-h.html) | `val RELATIVE_PATH: Key<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |

### Functions

| [pathMatcher](path-matcher.html) | `fun pathMatcher(pattern: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`PathMatcher`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html)<br>Converts given [pattern](path-matcher.html#io.gitlab.arturbosch.detekt.api.internal$pathMatcher(kotlin.String)/pattern) into a [PathMatcher](https://docs.oracle.com/javase/8/docs/api/java/nio/file/PathMatcher.html) specified by [FileSystem.getPathMatcher](https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)). We only support the "glob:" syntax to stay os independently. Internally a globbing pattern is transformed to a regex respecting the Windows file system. |

