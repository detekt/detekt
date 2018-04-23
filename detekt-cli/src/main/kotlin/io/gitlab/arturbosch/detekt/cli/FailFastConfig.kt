package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config

/**
 * @author vanniktech
 */
data class FailFastConfig(private val originalConfig: Config, private val defaultConfig: Config) : Config {
	override fun subConfig(key: String) = FailFastConfig(originalConfig.subConfig(key), defaultConfig.subConfig(key))

	override fun <T : Any> valueOrDefault(key: String, default: T): T {
		@Suppress("UNCHECKED_CAST")
		return when (key) {
			"active" -> originalConfig.valueOrDefault(key, true) as T
			"maxIssues" -> originalConfig.valueOrDefault(key, 0) as T
			else -> originalConfig.valueOrDefault(key, defaultConfig.valueOrDefault(key, default))
		}
	}
}
