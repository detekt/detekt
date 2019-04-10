package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.cli.LogLevel.DEBUG
import io.gitlab.arturbosch.detekt.cli.LogLevel.ERROR
import io.gitlab.arturbosch.detekt.cli.LogLevel.INFO
import io.gitlab.arturbosch.detekt.cli.LogLevel.VERBOSE
import io.gitlab.arturbosch.detekt.cli.LogLevel.WARN
import java.io.PrintStream

/**
 * @author Artur Bosch
 */
@Suppress("TooManyFunctions")
object LOG {
    var level: LogLevel = INFO
    var printer: PrintStream = System.out

    fun verbose(message: Any?, newLine: Boolean = true) = log(VERBOSE, message, newLine)
    fun verbose(newLine: Boolean = true, message: () -> Any?) = log(VERBOSE, message, newLine)

    fun debug(message: Any?, newLine: Boolean = true) = log(DEBUG, message, newLine)
    fun debug(newLine: Boolean = true, message: () -> Any?) = log(DEBUG, message, newLine)

    fun info(message: Any?, newLine: Boolean = true) = log(INFO, message, newLine)
    fun info(newLine: Boolean = true, message: () -> Any?) = log(INFO, message, newLine)

    fun warn(message: Any?, newLine: Boolean = true) = log(WARN, message, newLine)
    fun warn(newLine: Boolean = true, message: () -> Any?) = log(WARN, message, newLine)

    fun error(message: Any?, newLine: Boolean = true) = log(ERROR, message, newLine)
    fun error(newLine: Boolean = true, message: () -> Any?) = log(ERROR, message, newLine)

    private fun log(level: LogLevel, message: Any?, newLine: Boolean) {
        if (level >= this.level) {
            if (newLine) {
                printer.println(message)
            } else {
                printer.print(message)
            }
        }
    }
    private inline fun log(level: LogLevel, message: () -> Any?, newLine: Boolean) {
        if (level >= this.level) {
            if (newLine) {
                printer.println(message())
            } else {
                printer.print(message())
            }
        }
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
