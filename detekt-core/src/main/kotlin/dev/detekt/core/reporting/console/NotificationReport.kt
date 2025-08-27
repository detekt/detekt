package dev.detekt.core.reporting.console

import dev.detekt.api.ConsoleReport
import dev.detekt.api.Detektion
import dev.detekt.core.reporting.NotificationsKey

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
        val notifications = detektion.getUserData(NotificationsKey)
        if (notifications.isNullOrEmpty()) {
            return null
        }
        return notifications.joinToString(System.lineSeparator()) { it.message }
    }
}
