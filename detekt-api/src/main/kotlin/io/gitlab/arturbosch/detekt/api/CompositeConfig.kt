package io.gitlab.arturbosch.detekt.api

/**
 * @author Artur Bosch
 */
class CompositeConfig(private val lookFirst: Config, private val lookSecond: Config) : Config {

	override fun subConfig(key: String): Config
			= CompositeConfig(lookFirst.subConfig(key), lookSecond.subConfig(key))

	override fun <T : Any> valueOrDefault(key: String, default: T): T {
		val firstResult = lookFirst.valueOrDefault(key, default)
		return if (firstResult != default) firstResult else lookSecond.valueOrDefault(key, default)
	}

	override fun toString() = "CompositeConfig(lookFirst=$lookFirst, lookSecond=$lookSecond)"

}
