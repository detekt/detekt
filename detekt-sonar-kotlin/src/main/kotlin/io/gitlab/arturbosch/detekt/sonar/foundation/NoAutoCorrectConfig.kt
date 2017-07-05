package io.gitlab.arturbosch.detekt.sonar.foundation

import io.gitlab.arturbosch.detekt.api.Config

/**
 * Config wrapper for disabling automatic correction
 */
@Suppress("UNCHECKED_CAST")
class NoAutoCorrectConfig(private val config: Config) : Config {

	override fun subConfig(key: String): Config = config.subConfig(key)

	override fun <T : Any> valueOrDefault(key: String, default: T): T {
		if ("autoCorrect" == key) {
			return false as T
		}
		return config.valueOrDefault(key, default)
	}

}
