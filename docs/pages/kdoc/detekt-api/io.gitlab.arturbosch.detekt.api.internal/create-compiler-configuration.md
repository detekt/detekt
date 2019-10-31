---
title: createCompilerConfiguration - detekt-api
---

[detekt-api](../index.html) / [io.gitlab.arturbosch.detekt.api.internal](index.html) / [createCompilerConfiguration](./create-compiler-configuration.html)

# createCompilerConfiguration

`fun createCompilerConfiguration(pathsToAnalyze: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`Path`](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Path.html)`>, classpath: `[`List`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>, languageVersion: LanguageVersion?, jvmTarget: JvmTarget): CompilerConfiguration`

Creates a compiler configuration for the kotlin compiler with all known sources and classpath jars.
Be aware that if any path of [pathsToAnalyze](create-compiler-configuration.html#io.gitlab.arturbosch.detekt.api.internal$createCompilerConfiguration(kotlin.collections.List((java.nio.file.Path)), kotlin.collections.List((kotlin.String)), org.jetbrains.kotlin.config.LanguageVersion, org.jetbrains.kotlin.config.JvmTarget)/pathsToAnalyze) is a directory it is scanned for java and kotlin files.

