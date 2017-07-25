package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.com.intellij.openapi.util.Key

/**
 * @author Artur Bosch
 */
interface Detektion {
	val findings: Map<String, List<Finding>>
	val notifications: List<Notification>

	fun <V> getData(key: Key<V>): V?
	fun <V> addData(key: Key<V>, value: V)
}
