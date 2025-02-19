package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.ExecutionSpec
import java.util.concurrent.ExecutorService

@ProcessingModelDsl
class ExecutionSpecBuilder : Builder<ExecutionSpec> {

    var executorService: ExecutorService? = null
    var parallelParsing: Boolean = false
    var parallelAnalysis: Boolean = false
    override fun build(): ExecutionSpec = ExecutionModel(executorService, parallelParsing, parallelAnalysis)
}

private data class ExecutionModel(
    override val executorService: ExecutorService?,
    override val parallelParsing: Boolean,
    override val parallelAnalysis: Boolean,
) : ExecutionSpec
