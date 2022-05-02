package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.CompilerSpec
import org.jetbrains.kotlin.config.JvmTarget

@ProcessingModelDsl
class CompilerSpecBuilder : Builder<CompilerSpec> {

    var jvmTarget: JvmTarget = JvmTarget.JVM_1_8
    var languageVersion: String? = null
    var classpath: String? = null

    override fun build(): CompilerSpec = CompilerModel(jvmTarget, languageVersion, classpath)
}

private data class CompilerModel(
    override val jvmTarget: JvmTarget,
    override val languageVersion: String?,
    override val classpath: String?
) : CompilerSpec
