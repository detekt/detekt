package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.CompilerSpec

@ProcessingModelDsl
class CompilerSpecBuilder : Builder<CompilerSpec>, CompilerSpec {

    override var jvmTarget: String = "1.8"
    override var languageVersion: String? = null
    override var classpath: String? = null

    override fun build(): CompilerSpec = CompilerModel(jvmTarget, languageVersion, classpath)
}

internal data class CompilerModel(
    override val jvmTarget: String?,
    override val languageVersion: String?,
    override val classpath: String?
) : CompilerSpec
