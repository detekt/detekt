package dev.detekt.api

/**
 * Translates detekt's result container - [Detektion] - into an output report
 * which is written inside a file.
 */
interface OutputReport : Extension {

    /**
     * Supported ending of this report type.
     */
    val ending: String

    /**
     * Defines the translation process of detekt's result into a string.
     */
    fun render(detektion: Detektion): String?
}
