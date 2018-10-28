package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.com.intellij.openapi.util.Key

/**
 * @author Artur Bosch
 */
interface Detektion {
	val findings: Map<RuleSetId, List<Finding>>
	val notifications: Collection<Notification>
	val metrics: Collection<ProjectMetric>

	fun <V> getData(key: Key<V>): V?
	fun <V> addData(key: Key<V>, value: V)
	fun add(notification: Notification)
	fun add(projectMetric: ProjectMetric)
}
