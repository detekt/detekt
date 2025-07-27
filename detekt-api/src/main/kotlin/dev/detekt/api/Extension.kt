package dev.detekt.api

/**
 * Defines extension points in detekt.
 * Currently supported extensions are:
 * - [FileProcessListener]
 * - [ConsoleReport]
 * - [OutputReport]
 * - [ConfigValidator]
 * - [ReportingExtension]
 */
interface Extension {
    /**
     * Name of the extension.
     */
    val id: String

    /**
     * Is used to run extensions in a specific order.
     * The higher the priority the sooner the extension will run in detekt's lifecycle.
     */
    val priority: Int get() = -1

    /**
     * Setup extension by querying common paths and config options.
     */
    fun init(context: SetupContext) {
        // implement for setup code
    }
}
