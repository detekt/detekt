package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.util.keyFMap.KeyFMap

open class TestDetektion(vararg findings: Finding) : Detektion {

    override val metrics: Collection<ProjectMetric> = listOf()
    override val findings: Map<String, List<Finding>> = findings.groupBy { it.id }
    override val notifications: List<Notification> = listOf()
    private var userData = KeyFMap.EMPTY_MAP

    override fun <V> getData(key: Key<V>): V? = userData.get(key)

    override fun <V> addData(key: Key<V>, value: V) {
        userData = userData.plus(key, value)
    }

    override fun add(notification: Notification) = throw UnsupportedOperationException("not implemented")
    override fun add(projectMetric: ProjectMetric) = throw UnsupportedOperationException("not implemented")
}
