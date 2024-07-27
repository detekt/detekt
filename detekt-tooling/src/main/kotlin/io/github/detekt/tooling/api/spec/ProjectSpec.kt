package io.github.detekt.tooling.api.spec

import io.github.detekt.tooling.api.AnalysisMode
import java.nio.file.Path

/**
 * Project specific configuration and paths.
 */
interface ProjectSpec {

    /**
     * A base path to relativize paths. Mostly used for generating path in the output or report.
     */
    val basePath: Path

    /**
     * Paths to analyze. Works with files and directories.
     */
    val inputPaths: Collection<Path>

    /**
     * The analysis mode used by detekt. 'light' mode means detekt will analyze code in individual files but cannot use
     * compiler information like types, symbols and smart casts. 'full' mode allows rules to use additional information
     * from the compiler when analyzing code.
     */
    val analysisMode: AnalysisMode
}
