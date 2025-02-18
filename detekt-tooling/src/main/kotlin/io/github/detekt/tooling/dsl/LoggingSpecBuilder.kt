package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.LoggingSpec

class LoggingSpecBuilder : Builder<LoggingSpec> {

    var debug: Boolean = false
    var outputChannel: Appendable = System.out
    var errorChannel: Appendable = System.err

    override fun build(): LoggingSpec = LoggingModel(debug, outputChannel, errorChannel)
}

private data class LoggingModel(
    override val debug: Boolean,
    override val outputChannel: Appendable,
    override val errorChannel: Appendable,
) : LoggingSpec
