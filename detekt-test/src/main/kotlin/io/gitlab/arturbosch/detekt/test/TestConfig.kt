package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.Config

/**
 * @author Artur Bosch
 */
class TestConfig(val values: Map<String, String>) : Config {

	override fun subConfig(key: String) = this

	@Suppress("UNCHECKED_CAST")
	override fun <T : Any> valueOrDefault(key: String, default: () -> T) = values[key] as T? ?: default()
}