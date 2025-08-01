package dev.detekt.tooling.api.spec

import java.nio.file.Path

/**
 * Configures output reports.
 */
interface ReportsSpec {

    /**
     * Spec to identify a report.
     *
     * Can be a provided report like 'xml' or 'html; or a custom defined
     * via the [dev.detekt.api.OutputReport] extension.
     */
    interface Report {

        /**
         * Used to identify a specific known report type.
         */
        val type: String

        /**
         * Where to print to report to.
         */
        val path: Path
    }

    /**
     * Reports to generate for current analysis run.
     */
    val reports: Collection<Report>
}
