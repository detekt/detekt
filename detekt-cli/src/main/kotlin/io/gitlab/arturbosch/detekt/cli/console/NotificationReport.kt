package io.gitlab.arturbosch.detekt.cli.console

import io.gitlab.arturbosch.detekt.api.Detektion

/**
 * @author Artur Bosch
 */
class NotificationReport : ConsoleReport() {

	override val priority: Int = 5

	override fun render(detektion: Detektion): String? {
		val notifications = detektion.notifications
		return notifications.joinToString("\n") { it.message }
	}
}
