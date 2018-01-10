package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Detektion
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.ProjectMetric
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.util.keyFMap.KeyFMap

/**
 * @author Artur Bosch
 */
data class DetektResult(override val findings: Map<String, List<Finding>>) : Detektion {

	override val notifications: MutableCollection<Notification> = ArrayList()
	private var userData = KeyFMap.EMPTY_MAP
	private val _metrics = ArrayList<ProjectMetric>()
	override val metrics: Collection<ProjectMetric> = _metrics

	override fun add(projectMetric: ProjectMetric) {
		_metrics.add(projectMetric)
	}

	override fun add(notification: Notification) {
		notifications.add(notification)
	}

	override fun <V> getData(key: Key<V>): V? = userData.get(key)

	override fun <V> addData(key: Key<V>, value: V) {
		userData = userData.plus(key, value)
	}
}
