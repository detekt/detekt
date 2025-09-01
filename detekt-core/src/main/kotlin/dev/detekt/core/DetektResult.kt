package dev.detekt.core

import dev.detekt.api.Detektion
import dev.detekt.api.Issue
import dev.detekt.api.Notification
import dev.detekt.api.ProjectMetric
import dev.detekt.api.RuleInstance

class DetektResult(
    override val issues: List<Issue>,
    override val rules: List<RuleInstance>,
) : Detektion {

    private val _notifications = ArrayList<Notification>()
    override val notifications: Collection<Notification> = _notifications

    private val _metrics = ArrayList<ProjectMetric>()
    override val metrics: Collection<ProjectMetric> = _metrics

    override val userData: MutableMap<String, Any> = mutableMapOf()

    override fun add(projectMetric: ProjectMetric) {
        _metrics.add(projectMetric)
    }

    override fun add(notification: Notification) {
        _notifications.add(notification)
    }
}
