package io.gitlab.arturbosch.detekt.core.util

import dev.detekt.api.Notification

internal data class SimpleNotification(
    override val message: String,
    override val level: Notification.Level = Notification.Level.Error,
) : Notification {

    override fun toString(): String = message
}
