package dev.detekt.tooling.dsl

import dev.detekt.tooling.api.spec.CompilerSpec
import java.nio.file.Path

@ProcessingModelDsl
class CompilerSpecBuilder : Builder<CompilerSpec> {

    var jvmTarget: String = "1.8"
    var languageVersion: String? = null
    var apiVersion: String? = null
    var classpath: String? = null
    var jdkHome: Path? = null
    var freeCompilerArgs: List<String> = emptyList()

    override fun build(): CompilerSpec =
        CompilerModel(jvmTarget, languageVersion, apiVersion, classpath, jdkHome, freeCompilerArgs)
}

private data class CompilerModel(
    override val jvmTarget: String,
    override val languageVersion: String?,
    override val apiVersion: String?,
    override val classpath: String?,
    override val jdkHome: Path?,
    override val freeCompilerArgs: List<String>,
) : CompilerSpec
