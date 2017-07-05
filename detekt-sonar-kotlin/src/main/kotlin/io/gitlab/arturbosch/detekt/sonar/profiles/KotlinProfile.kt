package io.gitlab.arturbosch.detekt.sonar.profiles

import io.gitlab.arturbosch.detekt.sonar.foundation.DETEKT_WAY
import io.gitlab.arturbosch.detekt.sonar.foundation.KOTLIN_KEY
import io.gitlab.arturbosch.detekt.sonar.rules.RULE_KEYS
import io.gitlab.arturbosch.detekt.sonar.rules.severityTranslations
import org.sonar.api.profiles.ProfileDefinition
import org.sonar.api.profiles.RulesProfile
import org.sonar.api.rules.Rule
import org.sonar.api.rules.RulePriority
import org.sonar.api.utils.ValidationMessages

/**
 * @author Artur Bosch
 */
class KotlinProfile : ProfileDefinition() {

	override fun createProfile(validation: ValidationMessages): RulesProfile {
		val profile = RulesProfile.create(DETEKT_WAY, KOTLIN_KEY)
		RULE_KEYS.filter { it.active }.forEach {
			val severity = severityTranslations[it.issue.severity]
			val priority = RulePriority.valueOfString(severity)
			profile.activateRule(Rule.create(it.repository(), it.rule()), priority)
		}
		return profile
	}

}