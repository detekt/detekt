package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.LogLevel.DEBUG
import io.gitlab.arturbosch.detekt.cli.LogLevel.ERROR
import io.gitlab.arturbosch.detekt.cli.LogLevel.INFO
import io.gitlab.arturbosch.detekt.cli.LogLevel.VERBOSE
import io.gitlab.arturbosch.detekt.cli.LogLevel.WARN
import io.gitlab.arturbosch.detekt.cli.LogLevel.NONE
import java.io.PrintStream

/**
 * @author Artur Bosch
 */
@Suppress("TooManyFunctions")
object LOG {
    @Deprecated("Use LOG.level instead", ReplaceWith("LOG.level", "io.gitlab.arturbosch.detekt.cli.LOG"))
    var active: Boolean
        get() = level != NONE
        set(value) {
            level = when {
                value -> {
                    // Only reset the level if log is not set
                    if (level == NONE) INFO else level
                }
                else -> NONE
            }
        }
    var level: LogLevel = INFO
    var printer: PrintStream = System.out

    fun verbose(message: Any?) = log(VERBOSE, message)
    fun verbose(message: () -> Any?) = log(VERBOSE, message)

    fun debug(message: Any?) = log(DEBUG, message)
    fun debug(message: () -> Any?) = log(DEBUG, message)

    fun info(message: Any?) = log(INFO, message)
    fun info(message: () -> Any?) = log(INFO, message)

    fun warn(message: Any?) = log(WARN, message)
    fun warn(message: () -> Any?) = log(WARN, message)

    fun error(message: Any?) = log(ERROR, message)
    fun error(message: () -> Any?) = log(ERROR, message)

    private fun log(level: LogLevel, message: Any?) {
        if (level >= this.level) printer.println(message)
    }
    private inline fun log(level: LogLevel, message: () -> Any?) {
        if (level >= this.level) printer.println(message.invoke())
    }
}

enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    NONE
}
