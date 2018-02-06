package io.gitlab.arturbosch.detekt.extensions

/**
 * @author Artur Bosch
 */
object ProfileStorage {

	private val profiles: MutableSet<ProfileExtension> = mutableSetOf()
	val all = profiles
	val defaultProfile: ProfileExtension = ProfileExtension(DEFAULT_PROFILE_NAME)

	init {
		profiles.add(defaultProfile)
	}

	fun add(profile: ProfileExtension) {
		profiles.add(profile)
	}

	fun getByName(name: String) = profiles.find { it.name == name }
}
