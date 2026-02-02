package dev.detekt.tooling.api.spec

import dev.detekt.tooling.api.AnalysisMode
import java.nio.file.Path

/**
 * Project specific configuration and paths.
 */
interface ProjectSpec {

    /**
     * A base path to relativize paths. Mostly used for generating path in the output or report.
     *
     * It is always an absolute path.
     */
    val basePath: Path

    /**
     * Paths to analyze. Works with files and directories.
     */
    val inputPaths: Collection<Path>

    /**
     * The analysis mode used by detekt. See [AnalysisMode] for information about available analysis modes.
     */
    val analysisMode: AnalysisMode

    /**
     * Analyze Kotlin source files and collect diagnostics.
     */
    val diagnostics: Boolean
}
