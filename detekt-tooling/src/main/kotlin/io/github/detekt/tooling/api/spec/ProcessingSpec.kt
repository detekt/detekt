package io.github.detekt.tooling.api.spec

import io.github.detekt.tooling.dsl.ProcessingSpecBuilder
import io.github.detekt.tooling.dsl.processingSpec
import io.gitlab.arturbosch.detekt.api.UnstableApi

/**
 * Umbrella settings for detekt.
 */
interface ProcessingSpec {

    /**
     * Print debug messages like time measuring and extension loading.
     */
    val debug: Boolean

    /**
     * Should detekt create mutable ASTs which [io.gitlab.arturbosch.detekt.api.Rule]s can manipulate?
     */
    @UnstableApi
    val autoCorrect: Boolean

    val baselineSpec: BaselineSpec
    val compilerSpec: CompilerSpec
    val configSpec: ConfigSpec
    val executionSpec: ExecutionSpec
    val extensionsSpec: ExtensionsSpec
    val projectSpec: ProjectSpec
    val reportsSpec: ReportsSpec

    companion object {

        operator fun invoke(init: ProcessingSpecBuilder.() -> Unit): ProcessingSpec = processingSpec(init)
    }
}
