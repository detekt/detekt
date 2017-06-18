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
	val systemProfile get() = profiles.find { it.name == specifiedProfileNameThroughSystemProperty() }
	val systemOrDefaultProfile get() = systemProfile ?: defaultProfile

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
		val arguments = createArgumentsForProfile()
		return if (arguments.isNotEmpty()) arguments else project.fallbackArguments()
	}

	private fun createArgumentsForProfile(): List<String> {
		val allArguments = defaultProfile?.arguments() ?: mutableMapOf<String, String>()
		val overriddenArguments = systemProfile?.arguments() ?: mutableMapOf<String, String>()
		overriddenArguments.forEach {
			allArguments.merge(it.key, it.value) { v1, v2 ->
				multipleConfigAware(it.key, v1, v2)
			}
		}
		return allArguments.flatMap { filterBooleans(it.key, it.value) }.apply {
			if (debug) {
				val name = systemOrDefaultProfile?.name ?: "_fallback_"
				println("detekt version: $version - usedProfile: $name")
				println("Arguments: $this")
			}
		}
	}

	private fun filterBooleans(key: String, value: String)
			= if (value == "true" || value == "false") listOf(key) else listOf(key, value)

	private fun multipleConfigAware(key: String, v1: String, v2: String)
			= if (key == CONFIG || key == CONFIG_RESOURCE) "$v1,$v2" else v2

	private fun Project.fallbackArguments() = listOf(
			"--project", projectDir.absolutePath,
			CONFIG_RESOURCE, "/default-detekt-config.yml",
			"--filters", ".*/test/.*,.*/resources/.*,.*/build/.*,.*/target/.*")

	private fun specifiedProfileNameThroughSystemProperty(): String = System.getProperty(DETEKT_PROFILE) ?: profile

	override fun toString(): String = this.reflectiveToString()

}
