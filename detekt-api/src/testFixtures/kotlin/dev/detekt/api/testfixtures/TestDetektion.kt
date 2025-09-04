package dev.detekt.api.testfixtures

import com.intellij.openapi.util.Key
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
    userData: Map<String, Any> = emptyMap(),
) : Detektion {

    override val issues: List<Issue> = issues.toList()
    override val metrics: Collection<ProjectMetric> get() = _metrics
    override val notifications: List<Notification> get() = _notifications
    override val userData: MutableMap<String, Any> = userData.toMutableMap()

    private val _metrics = metrics.toMutableList()
    private val _notifications = notifications.toMutableList()

    fun <V> removeData(key: Key<V>) {
        userData.remove(key.toString())
    }

    override fun add(notification: Notification) {
        _notifications.add(notification)
    }

    override fun add(projectMetric: ProjectMetric) {
        _metrics.add(projectMetric)
    }
}
