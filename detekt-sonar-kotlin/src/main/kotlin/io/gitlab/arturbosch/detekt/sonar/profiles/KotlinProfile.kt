package io.gitlab.arturbosch.detekt.sonar.profiles

import io.gitlab.arturbosch.detekt.sonar.foundation.DETEKT_WAY
import io.gitlab.arturbosch.detekt.sonar.foundation.KOTLIN_KEY
import io.gitlab.arturbosch.detekt.sonar.rules.RULE_KEYS
import org.sonar.api.profiles.ProfileDefinition
import org.sonar.api.profiles.RulesProfile
import org.sonar.api.rules.Rule
import org.sonar.api.utils.ValidationMessages

/**
 * @author Artur Bosch
 */
class KotlinProfile : ProfileDefinition() {

	override fun createProfile(validation: ValidationMessages): RulesProfile {
		val profile = RulesProfile.create(DETEKT_WAY, KOTLIN_KEY)
		RULE_KEYS.forEach {
			profile.activateRule(Rule.create(it.repository(), it.rule()), null)
		}
		return profile
	}

}