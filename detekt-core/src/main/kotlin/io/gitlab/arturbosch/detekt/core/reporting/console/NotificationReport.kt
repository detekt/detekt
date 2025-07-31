package io.gitlab.arturbosch.detekt.core.reporting.console

import dev.detekt.api.ConsoleReport
import dev.detekt.api.Detektion

/**
 * Contains notifications reported by the detekt analyzer.
 * See: https://detekt.dev/configurations.html#console-reports
 */
class NotificationReport : ConsoleReport {

    override val id: String = "NotificationReport"

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
