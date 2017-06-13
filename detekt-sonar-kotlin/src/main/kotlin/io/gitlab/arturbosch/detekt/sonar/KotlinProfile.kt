package io.gitlab.arturbosch.detekt.sonar

import org.sonar.api.profiles.ProfileDefinition
import org.sonar.api.profiles.RulesProfile
import org.sonar.api.utils.ValidationMessages

/**
 * @author Artur Bosch
 */
class KotlinProfile : ProfileDefinition() {

	override fun createProfile(validation: ValidationMessages): RulesProfile
			= RulesProfile.create(KOTLIN_NAME, KOTLIN_NAME)

}