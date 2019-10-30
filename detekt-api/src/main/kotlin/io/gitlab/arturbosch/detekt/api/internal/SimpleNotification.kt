package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.Notification

data class SimpleNotification(override val message: String) : Notification {

    override fun toString(): String = message
}
