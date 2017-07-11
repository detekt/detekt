package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.BaseConfig

/**
 * @author Artur Bosch
 */
class TestConfig(val values: Map<String, String>) : BaseConfig() {

	override fun subConfig(key: String) = this

	@Suppress("UNCHECKED_CAST")
	override fun <T : Any> valueOrDefault(key: String, default: T)
			= valueOrDefaultInternal(values[key], default) as T
}
