package dev.detekt.api.test

import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase
import dev.detekt.api.Detektion
import dev.detekt.api.Issue
import dev.detekt.api.Notification
import dev.detekt.api.ProjectMetric
import dev.detekt.api.RuleInstance

class TestDetektion(
    vararg issues: Issue,
    override val rules: List<RuleInstance> = emptyList(),
    metrics: List<ProjectMetric> = emptyList(),
    notifications: List<Notification> = emptyList(),
) : Detektion, UserDataHolderBase() {

    override val issues: List<Issue> = issues.toList()
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
