package dev.detekt.core.settings

import dev.detekt.tooling.api.spec.LoggingSpec
import java.io.PrintStream
import java.io.PrintWriter

interface LoggingAware {

    val outputChannel: Appendable
    val errorChannel: Appendable

    fun info(msg: String)
    fun error(msg: String, error: Throwable)
    fun debug(msg: () -> String)
}

internal fun Throwable.printStacktraceRecursively(logger: Appendable) {
    when (logger) {
        is PrintStream -> this.printStackTrace(logger)

        is PrintWriter -> this.printStackTrace(logger)

        else -> {
            stackTrace.forEach { logger.appendLine(it.toString()) }
            cause?.printStacktraceRecursively(logger)
        }
    }
}

internal class LoggingFacade(val spec: LoggingSpec) : LoggingAware {

    override val outputChannel: Appendable = spec.outputChannel
    override val errorChannel: Appendable = spec.errorChannel

    override fun info(msg: String) {
        outputChannel.appendLine(msg)
    }

    override fun error(msg: String, error: Throwable) {
        errorChannel.appendLine(msg)
        error.printStacktraceRecursively(errorChannel)
    }

    override fun debug(msg: () -> String) {
        if (spec.debug) {
            outputChannel.appendLine(msg())
        }
    }
}
