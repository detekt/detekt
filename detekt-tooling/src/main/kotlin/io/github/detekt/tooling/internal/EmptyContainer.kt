package io.github.detekt.tooling.internal

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding2
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import org.jetbrains.kotlin.com.intellij.openapi.util.Key

object EmptyContainer : Detektion {

    override val findings: List<Finding2> = emptyList()
    override val notifications: Collection<Notification> = emptyList()
    override val metrics: Collection<ProjectMetric> = emptyList()

    override fun <V> getUserData(key: Key<V>): V? = throw UnsupportedOperationException()
    override fun <V> putUserData(key: Key<V>, value: V?) = throw UnsupportedOperationException()
    override fun add(notification: Notification) = throw UnsupportedOperationException()
    override fun add(projectMetric: ProjectMetric) = throw UnsupportedOperationException()
}
