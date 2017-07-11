package io.gitlab.arturbosch.detekt.extensions

import kotlin.reflect.full.memberProperties

/**
 * @author Artur Bosch
 */
fun Any.reflectiveToString(): String {
	val simpleName = this.javaClass.simpleName
	val properties = this.javaClass.kotlin.memberProperties.asSequence()
			.map { it.name to it.get(this) }
			.filter { it.second != null }
			.joinToString(" ") { "${it.first}=${it.second}" }
	return "$simpleName($properties)"
}
