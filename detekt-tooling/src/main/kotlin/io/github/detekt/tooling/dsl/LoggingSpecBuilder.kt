package io.github.detekt.tooling.dsl

import io.github.detekt.tooling.api.spec.LoggingSpec

class LoggingSpecBuilder : Builder<LoggingSpec> {

    var debug: Boolean = false
    var outputChannel: Appendable? = null
    var errorChannel: Appendable? = null

    override fun build(): LoggingSpec = LoggingModel(debug, outputChannel, errorChannel)
}

internal data class LoggingModel(
    override val debug: Boolean,
    override val outputChannel: Appendable?,
    override val errorChannel: Appendable?,
) : LoggingSpec
