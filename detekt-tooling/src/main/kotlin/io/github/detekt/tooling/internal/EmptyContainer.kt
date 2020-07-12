package io.github.detekt.tooling.internal

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.RuleSetId
import org.jetbrains.kotlin.com.intellij.openapi.util.Key

object EmptyContainer : Detektion {

    override val findings: Map<RuleSetId, List<Finding>> = emptyMap()
    override val notifications: Collection<Notification> = emptyList()
    override val metrics: Collection<ProjectMetric> = emptyList()

    override fun <V> getData(key: Key<V>): V? = throw UnsupportedOperationException()
    override fun <V> addData(key: Key<V>, value: V) = throw UnsupportedOperationException()
    override fun add(notification: Notification) = throw UnsupportedOperationException()
    override fun add(projectMetric: ProjectMetric) = throw UnsupportedOperationException()
}
