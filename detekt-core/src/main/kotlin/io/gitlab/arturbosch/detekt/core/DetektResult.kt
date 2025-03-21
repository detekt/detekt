package io.gitlab.arturbosch.detekt.core

import com.intellij.openapi.util.UserDataHolderBase
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.RuleInstance

@Suppress("DataClassShouldBeImmutable")
data class DetektResult(
    override val issues: List<Issue>,
    override val rules: List<RuleInstance>,
) : Detektion, UserDataHolderBase() {

    private val _notifications = ArrayList<Notification>()
    override val notifications: Collection<Notification> = _notifications

    private val _metrics = ArrayList<ProjectMetric>()
    override val metrics: Collection<ProjectMetric> = _metrics

    override fun add(projectMetric: ProjectMetric) {
        _metrics.add(projectMetric)
    }

    override fun add(notification: Notification) {
        _notifications.add(notification)
    }
}
