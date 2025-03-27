package io.gitlab.arturbosch.detekt.test

import com.intellij.openapi.util.Key
import com.intellij.openapi.util.UserDataHolderBase
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.RuleInstance

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
