package io.github.detekt.tooling.internal

import com.intellij.openapi.util.Key
import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.RuleInstance

object EmptyContainer : Detektion {

    override val issues: List<Issue> = emptyList()
    override val rules: List<RuleInstance> = emptyList()
    override val notifications: Collection<Notification> = emptyList()
    override val metrics: Collection<ProjectMetric> = emptyList()

    override fun <V> getUserData(key: Key<V>): V? = throw UnsupportedOperationException()
    override fun <V> putUserData(key: Key<V>, value: V?) = throw UnsupportedOperationException()
    override fun add(notification: Notification) = throw UnsupportedOperationException()
    override fun add(projectMetric: ProjectMetric) = throw UnsupportedOperationException()
}
