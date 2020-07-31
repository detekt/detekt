package io.gitlab.arturbosch.detekt.core.reporting.console

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion

/**
 * Contains notifications reported by the detekt analyzer.
 * See: https://detekt.github.io/detekt/configurations.html#console-reports
 */
class NotificationReport : ConsoleReport() {

    /**
     * Print notifications before the build failure report but after all other reports.
     * This allows to compute intermediate messages based on detekt results and do not rely on 'println'.
     */
    override val priority: Int = Int.MIN_VALUE + 1

    override fun render(detektion: Detektion): String? {
        if (detektion.notifications.isEmpty()) {
            return null
        }
        return detektion.notifications.joinToString(System.lineSeparator()) { it.message }
    }
}
