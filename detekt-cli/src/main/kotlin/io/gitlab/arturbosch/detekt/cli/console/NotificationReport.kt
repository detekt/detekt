package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.ConsoleReport
import io.gitlab.arturbosch.detekt.api.Detektion

class NotificationReport : ConsoleReport() {

    override val priority: Int = 50

    override fun render(detektion: Detektion): String? {
        val notifications = detektion.notifications
        return notifications.joinToString("\n") { it.message }
    }
}
