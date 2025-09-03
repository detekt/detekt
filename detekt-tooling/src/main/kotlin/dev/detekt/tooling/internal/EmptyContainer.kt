package dev.detekt.tooling.internal

import dev.detekt.api.Detektion
import dev.detekt.api.Issue
import dev.detekt.api.Notification
import dev.detekt.api.ProjectMetric
import dev.detekt.api.RuleInstance

object EmptyContainer : Detektion {

    override val issues: List<Issue> = emptyList()
    override val rules: List<RuleInstance> = emptyList()
    override val notifications: Collection<Notification> = emptyList()
    override val metrics: Collection<ProjectMetric> = emptyList()
    override val userData: MutableMap<String, Any> = mutableMapOf()

    override fun add(notification: Notification) = throw UnsupportedOperationException()
    override fun add(projectMetric: ProjectMetric) = throw UnsupportedOperationException()
}
