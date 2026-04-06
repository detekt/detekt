package dev.detekt.tooling.dsl

import dev.detekt.tooling.api.spec.CompilerSpec
import java.nio.file.Path

@ProcessingModelDsl
class CompilerSpecBuilder : Builder<CompilerSpec> {

    var jvmTarget: String = "1.8"
    var languageVersion: String? = null
    var apiVersion: String? = null
    var classpath: List<Path> = emptyList()
    var jdkHome: Path? = null
    var freeCompilerArgs: List<String> = emptyList()
    var compilerPluginClasspath: List<Path> = emptyList()

    override fun build(): CompilerSpec =
        CompilerModel(
            jvmTarget = jvmTarget,
            languageVersion = languageVersion,
            apiVersion = apiVersion,
            classpath = classpath,
            jdkHome = jdkHome,
            freeCompilerArgs = freeCompilerArgs,
            compilerPluginClasspath = compilerPluginClasspath,
        )
}

private data class CompilerModel(
    override val jvmTarget: String,
    override val languageVersion: String?,
    override val apiVersion: String?,
    override val classpath: List<Path>,
    override val jdkHome: Path?,
    override val freeCompilerArgs: List<String>,
    override val compilerPluginClasspath: List<Path>,
) : CompilerSpec
