package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.CompilerSpec

@ProcessingModelDsl
class CompilerSpecBuilder : Builder<CompilerSpec> {

    var jvmTarget: String = "1.8"
    var languageVersion: String? = null
    var classpath: String? = null

    override fun build(): CompilerSpec = CompilerModel(jvmTarget, languageVersion, classpath)
}

private data class CompilerModel(
    override val jvmTarget: String?,
    override val languageVersion: String?,
    override val classpath: String?
) : CompilerSpec
