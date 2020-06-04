package io.gitlab.arturbosch.detekt.api

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
    val id: String get() = javaClass.simpleName

    /**
     * Is used to run extensions in a specific order.
     * The higher the priority the sooner the extension will run in detekt's lifecycle.
     */
    val priority: Int get() = -1

    /**
     * Allows to read any or even user defined properties from the detekt yaml config
     * to setup this extension.
     */
    fun init(config: Config) {
        // implement for setup code
    }

    /**
     * Setup extension by querying common paths and config options.
     */
    @OptIn(UnstableApi::class)
    fun init(context: SetupContext) {
        // implement for setup code
    }
}
