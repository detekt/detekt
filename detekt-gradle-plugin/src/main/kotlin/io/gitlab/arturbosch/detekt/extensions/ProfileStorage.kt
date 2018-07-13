package io.gitlab.arturbosch.detekt.extensions

/**
 * @author Artur Bosch
 */
class ProfileStorage {

	private val profiles: MutableSet<ProfileExtension> = mutableSetOf()
	val all: Set<ProfileExtension> = profiles

	internal var defaultProfile = ProfileExtension.default()

	val systemProfile: ProfileExtension?
		get() = System.getProperty(DETEKT_PROFILE)?.let { getByName(it) }

	val systemOrDefault get() = systemProfile ?: defaultProfile

	init {
		profiles.add(defaultProfile)
	}

	fun add(profile: ProfileExtension) {
		profiles.add(profile)
	}

	fun getByName(name: String) = profiles.find { it.name == name }
}
