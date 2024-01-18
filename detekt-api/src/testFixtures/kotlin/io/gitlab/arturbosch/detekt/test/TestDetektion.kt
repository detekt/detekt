package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.openapi.util.UserDataHolderBase

class TestDetektion(
    vararg findings: Finding2,
    metrics: List<ProjectMetric> = emptyList(),
    notifications: List<Notification> = emptyList(),
) : Detektion, UserDataHolderBase() {

    override val findings: List<Finding2> = findings.toList()
    override val metrics: Collection<ProjectMetric> get() = _metrics
    override val notifications: List<Notification> get() = _notifications

    private val _metrics = metrics.toMutableList()
    private val _notifications = notifications.toMutableList()

    fun <V> removeData(key: Key<V>) {
        putUserData(key, null)
    }

    override fun add(notification: Notification) {
        _notifications.add(notification)
    }

    override fun add(projectMetric: ProjectMetric) {
        _metrics.add(projectMetric)
    }
}
