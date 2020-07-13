package io.github.detekt.tooling.api.spec

import io.github.detekt.tooling.dsl.ProcessingSpecBuilder
import io.github.detekt.tooling.dsl.processingSpec

/**
 * Settings to use for a detekt run.
 */
interface ProcessingSpec {

    val baselineSpec: BaselineSpec
    val compilerSpec: CompilerSpec
    val configSpec: ConfigSpec
    val executionSpec: ExecutionSpec
    val extensionsSpec: ExtensionsSpec
    val rulesSpec: RulesSpec
    val loggingSpec: LoggingSpec
    val projectSpec: ProjectSpec
    val reportsSpec: ReportsSpec

    companion object {

        operator fun invoke(init: ProcessingSpecBuilder.() -> Unit): ProcessingSpec = processingSpec(init)
    }
}
