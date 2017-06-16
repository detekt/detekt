package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Action
import org.gradle.api.Project

/**
 * @author Artur Bosch
 */
open class DetektExtension(open var version: String = "1.0.0.M11",
						   open var debug: Boolean = false,
						   open var profile: String = "main",
						   open var ideaExtension: IdeaExtension = IdeaExtension()) {

	val profiles: MutableList<ProfileExtension> = mutableListOf()
	val defaultProfile get() = profiles.find { it.name == profile }
	val systemOrDefaultProfile get() = profiles.find { it.name == specifiedProfileNameThroughSystemProperty() }
			?: defaultProfile

	val ideaFormatArgs get() = ideaExtension.formatArgs(this)
	val ideaInspectArgs get() = ideaExtension.inspectArgs(this)

	fun idea(configuration: Action<in IdeaExtension>) {
		configuration.execute(ideaExtension)
	}

	fun profile(name: String, configuration: Action<in ProfileExtension>) {
		ProfileExtension(name).apply {
			profiles.add(this)
			configuration.execute(this)
		}
	}

	fun profileArgumentsOrDefault(project: Project): List<String> {
		val systemProfile = specifiedProfileNameThroughSystemProperty()
		val argumentsForProfile = createArgumentsForProfile(systemProfile)
		return if (argumentsForProfile.isNotEmpty()) argumentsForProfile else {
			listOf("--project", project.projectDir.absolutePath, "--config-resource", "/default-detekt-config.yml")
		}
	}

	fun createArgumentsForProfile(name: String): List<String> {
		debugProfileArguments(name)
		val arguments = profiles.find { it.name == profile }?.arguments() ?: mutableMapOf<String, String>()
		val actualProfileArgs = profiles.find { it.name == name }?.arguments()
		actualProfileArgs?.let { it.forEach { arguments.merge(it.key, it.value) { _, v2 -> v2 } } }
		return arguments.flatMap { filterBooleans(it.key, it.value) }.apply {
			if (debug) println("Arguments: $this")
		}
	}

	private fun filterBooleans(key: String, value: String)
			= if (value == "true" || value == "false") listOf(key) else listOf(key, value)

	private fun debugProfileArguments(name: String) {
		if (debug) {
			val systemProfile = specifiedProfileNameThroughSystemProperty()
			println("detekt version: $version - requestedProfile: $name - usedProfile: $systemProfile" +
					"\n\t${profiles.find { it.name == systemProfile }}")
		}
	}

	private fun specifiedProfileNameThroughSystemProperty(): String = System.getProperty("detekt.profile") ?: profile

	override fun toString(): String = this.reflectiveToString()

}
