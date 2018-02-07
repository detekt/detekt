package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Action
import org.gradle.api.Project

/**
 * @author Artur Bosch
 * @author Said Tahsin Dane
 */
open class DetektExtension(open var version: String = SUPPORTED_DETEKT_VERSION,
						   open var debug: Boolean = DEFAULT_DEBUG_VALUE,
						   open var profile: String = DEFAULT_PROFILE_NAME,
						   open var ideaExtension: IdeaExtension = IdeaExtension()) {

	fun ideaFormatArgs() = ideaExtension.formatArgs(this)
	fun ideaInspectArgs() = ideaExtension.inspectArgs(this)

	fun idea(configuration: Action<in IdeaExtension>) {
		configuration.execute(ideaExtension)
	}

	fun defaultProfile(configuration: Action<in ProfileExtension>) {
		configuration.execute(ProfileStorage.defaultProfile)
	}

	fun profile(name: String, configuration: Action<in ProfileExtension>) {
		if (name == DEFAULT_PROFILE_NAME) {
			defaultProfile(configuration)
		} else {
			ProfileExtension(name).apply {
				ProfileStorage.add(this)
				configuration.execute(this)
			}
		}
	}

	fun resolveArguments(project: Project): List<String> {
		return with(extractArguments()) {
			if (!contains(INPUT_PARAMETER)) {
				add(INPUT_PARAMETER)
				add(project.projectDir.toString())
			}
			this
		}
	}

	private fun extractArguments(): MutableList<String> {
		val defaultProfile = ProfileStorage.defaultProfile
		val systemOrSelected = ProfileStorage.systemProfile
				?: ProfileStorage.getByName(profile)

		val propertyMap =
				if (systemOrSelected?.name == defaultProfile.name) {
					defaultProfile.arguments(debug)
				} else {
					defaultProfile.arguments(debug).apply {
						systemOrSelected?.arguments(debug)?.mergeInto(this)
					}
				}

		val arguments = propertyMap.flatMapTo(ArrayList()) { removeBooleanValues(it.key, it.value) }

		if (debug) {
			val name = systemOrSelected?.name ?: DEFAULT_PROFILE_NAME
			println("detekt version: $version - usedProfile: $name")
			println("arguments: $arguments")
		}

		return arguments
	}

	private fun removeBooleanValues(key: String, value: String) =
			if (value == "true" || value == "false") listOf(key) else listOf(key, value)

	override fun toString(): String = "DetektExtension(version='$version', " +
			"debug=$debug, profile='$profile', ideaExtension=$ideaExtension, profiles=${ProfileStorage.all})"
}

private fun MutableMap<String, String>.mergeInto(other: MutableMap<String, String>) {
	for ((key, value) in this) {
		other.merge(key, value) { v1, v2 ->
			joinMultipleConfigurations(key, v1, v2)
		}
	}
}

private fun joinMultipleConfigurations(key: String, v1: String, v2: String) =
		if (key == CONFIG_PARAMETER || key == CONFIG_RESOURCE_PARAMETER) "$v1,$v2" else v2
