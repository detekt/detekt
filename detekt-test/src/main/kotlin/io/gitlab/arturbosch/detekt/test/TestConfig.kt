package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.BaseConfig

/**
 * @author Artur Bosch
 */
class TestConfig(private val values: Map<String, String>) : BaseConfig() {

	override fun subConfig(key: String) = this

	@Suppress("UNCHECKED_CAST")
	override fun <T : Any> valueOrDefault(key: String, default: T) = when (key) {
		"active" -> getActiveValue(default) as T
		else -> valueOrDefaultInternal(values[key], default) as T
	}

	private fun <T : Any> getActiveValue(default: T): Any {
		val active = values["active"]
		return if (active != null) valueOrDefaultInternal(active, default) else true
	}
}
