package io.gitlab.arturbosch.detekt.api

import java.io.PrintStream

/**
 * Extension point which describes how findings should be printed on the console.
 *
 * Additional [ConsoleReport]'s can be made available through the [java.util.ServiceLoader] pattern.
 * If the default reporting mechanism should be turned off, exclude the entry 'FindingsReport'
 * in the 'console-reports' property of a detekt yaml config.
 */
abstract class ConsoleReport : Extension {

    /**
     * Prints the rendered report to the given printer
     * if anything was rendered at all.
     */
    @Deprecated("Use render to print the result to any Appendable.")
    fun print(printer: PrintStream, detektion: Detektion) {
        val output = render(detektion)
        if (!output.isNullOrBlank()) {
            printer.println(output)
        }
    }

    /**
     * Converts the given [detektion] into a string representation
     * to present it to the client.
     * The implementation specifies which parts of the report are important to the user.
     */
    abstract fun render(detektion: Detektion): String?
}
