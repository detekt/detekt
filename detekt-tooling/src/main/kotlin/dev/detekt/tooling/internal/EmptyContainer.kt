package dev.detekt.tooling.internal

import com.intellij.openapi.util.Key
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

    override fun <V> getUserData(key: Key<V>): V? = throw UnsupportedOperationException()
    override fun <V> putUserData(key: Key<V>, value: V?) = throw UnsupportedOperationException()
    override fun add(notification: Notification) = throw UnsupportedOperationException()
    override fun add(projectMetric: ProjectMetric) = throw UnsupportedOperationException()
}
