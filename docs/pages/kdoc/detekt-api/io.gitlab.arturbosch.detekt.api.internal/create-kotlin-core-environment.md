---
title: createKotlinCoreEnvironment - detekt-api
---

[detekt-api](../index.html) / [io.gitlab.arturbosch.detekt.api.internal](index.html) / [createKotlinCoreEnvironment](./create-kotlin-core-environment.html)

# createKotlinCoreEnvironment

`fun createKotlinCoreEnvironment(configuration: CompilerConfiguration = CompilerConfiguration(), disposable: Disposable = Disposer.newDisposable()): KotlinCoreEnvironment`

Creates an environment instance which can be used to compile source code to KtFile's.
This environment also allows to modify the resulting AST files.

