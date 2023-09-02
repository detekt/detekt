package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.openapi.util.UserDataHolderBase

open class TestDetektion(vararg findings: Finding) : Detektion, UserDataHolderBase() {

    override val findings: Map<String, List<Finding>> = findings.groupBy { it.id }
    override val notifications: List<Notification> get() = _notifications

    private val _notifications = mutableListOf<Notification>()

    fun <V> removeData(key: Key<V>) {
        putUserData(key, null)
    }

    override fun add(notification: Notification) {
        _notifications.add(notification)
    }
}
