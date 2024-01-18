package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.RuleSet
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.openapi.util.UserDataHolderBase

open class TestDetektion(
    vararg findings: Finding2,
    metrics: List<ProjectMetric> = emptyList(),
    notifications: List<Notification> = emptyList(),
) : Detektion, UserDataHolderBase() {

    override val findings: Map<RuleSet.Id, List<Finding2>> = findings.groupBy { it.ruleInfo.ruleSetId }
    final override val metrics: Collection<ProjectMetric> get() = _metrics
    final override val notifications: List<Notification> get() = _notifications

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
