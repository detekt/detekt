package io.gitlab.arturbosch.detekt.api

import java.nio.file.Files
import java.nio.file.Path

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
     * Name of the report. Is used to exclude this report in the yaml config.
     */
    open val name: String?
        get() = this::class.simpleName

    /**
     * Renders result and writes it to the given [filePath].
     */
    fun write(filePath: Path, detektion: Detektion) {
        val reportData = render(detektion)
        if (reportData != null) {
            assert(filePath.fileName.toString().endsWith(ending)) {
                "The $name needs to have a file ending of type .$ending, but was ${filePath.fileName}."
            }
            filePath.parent?.let { Files.createDirectories(it) }
            Files.write(filePath, reportData.toByteArray())
        }
    }

    /**
     * Defines the translation process of detekt's result into a string.
     */
    abstract fun render(detektion: Detektion): String?
}
