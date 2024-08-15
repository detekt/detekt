package io.gitlab.arturbosch.detekt.api

import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.extension
import kotlin.io.path.writeText

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
     * Renders result and writes it to the given [filePath].
     */
    fun write(filePath: Path, detektion: Detektion) {
        val reportData = render(detektion)
        if (reportData != null) {
            assert(filePath.extension == ending) {
                "The $id needs to have a file ending of type .$ending, but was ${filePath.fileName}."
            }
            filePath.createParentDirectories().writeText(reportData)
        }
    }

    /**
     * Defines the translation process of detekt's result into a string.
     */
    abstract fun render(detektion: Detektion): String?
}
