package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.quality.CodeQualityExtension
import org.gradle.api.resources.TextResource
import java.io.File

/**
 * @author Artur Bosch
 * @author Said Tahsin Dane
 * @author Marvin Ramin
 */
open class DetektExtension(val project: Project) : CodeQualityExtension() {

	open var version: String = SUPPORTED_DETEKT_VERSION
	open var debug: Boolean = DEFAULT_DEBUG_VALUE
	open var parallel: Boolean = false
	open var disableDefaultRuleSets: Boolean = false
	open var profile: String = DEFAULT_PROFILE_NAME
	open var profiles: List<ProfileExtension> = listOf()
	open var filters: String? = null
	open var baseline: File? = null
	open var plugins: String? = null
	open var ideaExtension: IdeaExtension = IdeaExtension()
	open lateinit var config: TextResource
	open lateinit var configDir: File
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

	fun activeProfile(): ProfileExtension? {
		println("Found ${profiles.size} profiles.")
		return profiles.find { it.name == profile }
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
		if (key == CONFIG_PARAMETER) "$v1,$v2" else v2
