package dev.detekt.api.testfixtures

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
) : Detektion, UserDataHolderBase() {

    override val issues: List<Issue> = issues.toList()
    override val metrics: Collection<ProjectMetric> get() = _metrics

    private val _metrics = metrics.toMutableList()

    fun <V> removeData(key: Key<V>) {
        putUserData(key, null)
    }

    override fun add(projectMetric: ProjectMetric) {
        _metrics.add(projectMetric)
    }
}
