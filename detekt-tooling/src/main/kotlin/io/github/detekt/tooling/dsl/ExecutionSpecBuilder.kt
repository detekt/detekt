package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.ExecutionSpec
import java.util.concurrent.ExecutorService

@ProcessingModelDsl
class ExecutionSpecBuilder : Builder<ExecutionSpec>, ExecutionSpec {

    override var executorService: ExecutorService? = null
    override var parallelParsing: Boolean = false
    override var parallelAnalysis: Boolean = false
    override fun build(): ExecutionSpec = ExecutionModel(executorService, parallelParsing, parallelAnalysis)
}

internal data class ExecutionModel(
    override val executorService: ExecutorService?,
    override val parallelParsing: Boolean,
    override val parallelAnalysis: Boolean
) : ExecutionSpec
