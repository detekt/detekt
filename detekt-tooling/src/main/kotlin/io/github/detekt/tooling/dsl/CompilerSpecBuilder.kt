package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.CompilerSpec

@ProcessingModelDsl
class CompilerSpecBuilder : Builder<CompilerSpec> {

    var jvmTarget: String = "1.8"
    var languageVersion: String? = null
    var classpath: String? = null
    var freeCompilerArgs: List<String> = emptyList()

    override fun build(): CompilerSpec = CompilerModel(jvmTarget, languageVersion, classpath, freeCompilerArgs)
}

private data class CompilerModel(
    override val jvmTarget: String,
    override val languageVersion: String?,
    override val classpath: String?,
    override val freeCompilerArgs: List<String>,
) : CompilerSpec
