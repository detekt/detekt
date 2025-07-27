package dev.detekt.api

/**
 * Extension point which describes how findings should be printed on the console.
 *
 * Additional [ConsoleReport]'s can be made available through the [java.util.ServiceLoader] pattern.
 * If the default reporting mechanism should be turned off, exclude the entry 'FindingsReport'
 * in the 'console-reports' property of a detekt yaml config.
 */
interface ConsoleReport : Extension {

    /**
     * Converts the given [detektion] into a string representation
     * to present it to the client.
     * The implementation specifies which parts of the report are important to the user.
     */
    fun render(detektion: Detektion): String?
}
