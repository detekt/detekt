package dev.detekt.tooling.api.spec

import java.nio.file.Path

interface BaselineSpec {

    /**
     * Path to your baseline file.
     *
     * A baseline is an additional mechanism to suppress issues without adding annotations or comments in your code.
     */
    val path: Path?

    /**
     * Should this analysis write all findings to a new baseline.
     */
    val shouldCreateDuringAnalysis: Boolean
}
