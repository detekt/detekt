package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import io.gitlab.arturbosch.detekt.api.RuleSetId
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.util.keyFMap.KeyFMap

@Suppress("DataClassShouldBeImmutable")
data class DetektResult(override val findings: Map<RuleSetId, List<Finding>>) : Detektion {

    private val _notifications = ArrayList<Notification>()
    override val notifications: Collection<Notification> = _notifications

    private val _metrics = ArrayList<ProjectMetric>()
    override val metrics: Collection<ProjectMetric> = _metrics

    private var userData = KeyFMap.EMPTY_MAP

    override fun add(projectMetric: ProjectMetric) {
        _metrics.add(projectMetric)
    }

    override fun add(notification: Notification) {
        _notifications.add(notification)
    }

    override fun <V> getData(key: Key<V>): V? = userData.get(key)

    override fun <V> addData(key: Key<V>, value: V) {
        userData = userData.plus(key, value)
    }
}
