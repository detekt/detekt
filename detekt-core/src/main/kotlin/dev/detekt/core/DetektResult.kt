package dev.detekt.core

import com.intellij.openapi.util.UserDataHolderBase
import dev.detekt.api.Detektion
import dev.detekt.api.Issue
import dev.detekt.api.Notification
import dev.detekt.api.ProjectMetric
import dev.detekt.api.RuleInstance

@Suppress("DataClassShouldBeImmutable")
data class DetektResult(
    override val issues: List<Issue>,
    override val rules: List<RuleInstance>,
) : Detektion, UserDataHolderBase() {

    private val _notifications = ArrayList<Notification>()

    private val _metrics = ArrayList<ProjectMetric>()
    override val metrics: Collection<ProjectMetric> = _metrics

    override fun add(projectMetric: ProjectMetric) {
        _metrics.add(projectMetric)
    }
}
