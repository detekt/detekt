package io.gitlab.arturbosch.detekt.extensions

/**
 * @author Artur Bosch
 */
object ProfileStorage {

	private val profiles: MutableSet<ProfileExtension> = mutableSetOf()
	val all = profiles

	val defaultProfile: ProfileExtension = ProfileExtension(DEFAULT_PROFILE_NAME)

	init {
		defaultProfile.filters = DEFAULT_PATH_EXCLUDES
		defaultProfile.configResource = DEFAULT_DETEKT_CONFIG_RESOURCE
	}

	val systemProfile: ProfileExtension?
		get() = System.getProperty(DETEKT_PROFILE)?.let { ProfileStorage.getByName(it) }

	val systemOrDefault get() = systemProfile ?: defaultProfile

	init {
		profiles.add(defaultProfile)
	}

	fun add(profile: ProfileExtension) {
		profiles.add(profile)
	}

	fun getByName(name: String) = profiles.find { it.name == name }
}
