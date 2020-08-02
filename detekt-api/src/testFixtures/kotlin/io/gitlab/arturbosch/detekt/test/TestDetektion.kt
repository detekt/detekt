package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.util.keyFMap.KeyFMap

open class TestDetektion(vararg findings: Finding) : Detektion {

    override val findings: Map<String, List<Finding>> = findings.groupBy { it.id }
    override val metrics: Collection<ProjectMetric> get() = _metrics
    override val notifications: List<Notification> get() = _notifications

    private var userData = KeyFMap.EMPTY_MAP
    private val _metrics = mutableListOf<ProjectMetric>()
    private val _notifications = mutableListOf<Notification>()

    override fun <V> getData(key: Key<V>): V? = userData.get(key)

    override fun <V> addData(key: Key<V>, value: V) {
        userData = userData.plus(key, value)
    }

    fun <V> removeData(key: Key<V>) {
        userData = userData.minus(key)
    }

    override fun add(notification: Notification) {
        _notifications.add(notification)
    }

    override fun add(projectMetric: ProjectMetric) {
        _metrics.add(projectMetric)
    }
}
