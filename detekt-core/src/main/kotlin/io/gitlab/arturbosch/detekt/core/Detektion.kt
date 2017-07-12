package io.gitlab.arturbosch.detekt.core

import io.gitlab.arturbosch.detekt.api.Finding
import org.jetbrains.kotlin.com.intellij.openapi.util.Key
import org.jetbrains.kotlin.com.intellij.util.keyFMap.KeyFMap

/**
 * @author Artur Bosch
 */
interface Detektion {
	val findings: Map<String, List<Finding>>
	val notifications: List<Notification>

	fun <V> getData(key: Key<V>): V?
	fun <V> addData(key: Key<V>, value: V)
}

data class DetektResult(override val findings: Map<String, List<Finding>>,
						override val notifications: List<Notification>) : Detektion {

	private var userData = KeyFMap.EMPTY_MAP

	override fun <V> getData(key: Key<V>): V? = userData.get(key)

	override fun <V> addData(key: Key<V>, value: V) {
		userData = userData.plus(key, value)
	}
}
