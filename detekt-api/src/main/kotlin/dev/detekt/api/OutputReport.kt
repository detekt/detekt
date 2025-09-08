package dev.detekt.api

/**
 * Translates detekt's result container - [Detektion] - into an output report
 * which is written inside a file.
 */
abstract class OutputReport : Extension {

    /**
     * Supported ending of this report type.
     */
    abstract val ending: String

    /**
     * Defines the translation process of detekt's result into a string.
     */
    abstract fun render(detektion: Detektion): String?
}
