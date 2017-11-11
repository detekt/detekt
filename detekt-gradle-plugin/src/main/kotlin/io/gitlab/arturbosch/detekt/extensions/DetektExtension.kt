package io.gitlab.arturbosch.detekt.extensions

import org.gradle.api.Action
import org.gradle.api.Project

/**
 * @author Artur Bosch
 */
open class DetektExtension(open var version: String = SUPPORTED_DETEKT_VERSION,
						   open var debug: Boolean = DEBUG_PARAMETER,
						   open var ideaExtension: IdeaExtension = IdeaExtension()) {

	private val profiles: MutableList<ProfileExtension> = mutableListOf()

	fun systemOrDefaultProfile() = getSystemProfile() ?: getDefaultProfile()
	fun ideaFormatArgs() = ideaExtension.formatArgs(this)
	fun ideaInspectArgs() = ideaExtension.inspectArgs(this)

	fun idea(configuration: Action<in IdeaExtension>) {
		configuration.execute(ideaExtension)
	}

	fun profile(name: String, configuration: Action<in ProfileExtension>) {
		ProfileExtension(name).apply {
			profiles.add(this)
			configuration.execute(this)
		}
	}

	fun getProfiles() = profiles

	fun profileArgumentsOrDefault(project: Project, profile: ProfileExtension): List<String> {
		return with(createArgumentsForProfile(profile)) {
			if (isNotEmpty()) {
				if (!contains(INPUT_PARAMETER)) {
					add(INPUT_PARAMETER)
					add(project.projectDir.toString())
				}
				this
			} else {
				project.fallbackArguments()
			}
		}
	}

	private fun createArgumentsForProfile(defaultProfile: ProfileExtension): MutableList<String> {
		val systemProfile = getSystemProfile()
		val mainProfile = if (defaultProfile.name != DEFAULT_PROFILE_NAME && systemProfile?.name != DEFAULT_PROFILE_NAME) {
			searchProfileWithName(DEFAULT_PROFILE_NAME)
		} else null

		val allArguments = mainProfile?.arguments(debug) ?: mutableMapOf()
		val defaultArguments = defaultProfile.arguments(debug) ?: mutableMapOf()
		val fallbackEmptyArguments = mutableMapOf<String, String>()

		val overriddenArguments = if (systemProfile?.name == defaultProfile.name) fallbackEmptyArguments
		else systemProfile?.arguments(debug) ?: fallbackEmptyArguments

		defaultArguments.merge(allArguments)
		overriddenArguments.merge(allArguments)

		return allArguments.flatMapTo(ArrayList()) { flattenBoolValues(it.key, it.value) }.apply {
			if (debug) {
				val name = systemOrDefaultProfile()?.name ?: "_fallback_"
				println("detekt version: $version - usedProfile: $name")
				println("Arguments: $this")
			}
		}
	}

	private fun searchProfileWithName(name: String) = profiles.find { it.name == name }
	private fun getDefaultProfile() = searchProfileWithName(DEFAULT_PROFILE_NAME)
	private fun getSystemProfile(): ProfileExtension? {
		return System.getProperty(DETEKT_PROFILE)?.let {
			searchProfileWithName(it)
		}
	}

	private fun flattenBoolValues(key: String, value: String)
			= if (value == "true" || value == "false") listOf(key) else listOf(key, value)

	override fun toString(): String = "DetektExtension(version='$version', " +
			"debug=$debug, ideaExtension=$ideaExtension, profiles=$profiles)"
}

private fun MutableMap<String, String>.merge(other: MutableMap<String, String>) {
	for ((key, value) in this) {
		other.merge(key, value) { v1, v2 ->
			multipleConfigAware(key, v1, v2)
		}
	}
}

private fun multipleConfigAware(key: String, v1: String, v2: String)
		= if (key == CONFIG_PARAMETER || key == CONFIG_RESOURCE_PARAMETER) "$v1,$v2" else v2

internal fun Project.fallbackArguments() = listOf(
		INPUT_PARAMETER, projectDir.absolutePath,
		CONFIG_RESOURCE_PARAMETER, DEFAULT_DETEKT_CONFIG_RESOURCE,
		FILTERS_PARAMETER, DEFAULT_PATH_EXCLUDES)
