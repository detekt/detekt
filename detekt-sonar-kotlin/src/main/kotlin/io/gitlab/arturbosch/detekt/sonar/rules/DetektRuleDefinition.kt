package io.gitlab.arturbosch.detekt.sonar.rules

import io.gitlab.arturbosch.detekt.sonar.foundation.DETEKT_ANALYZER
import io.gitlab.arturbosch.detekt.sonar.foundation.DETEKT_REPOSITORY
import io.gitlab.arturbosch.detekt.sonar.foundation.KOTLIN_KEY
import org.sonar.api.server.rule.RulesDefinition

/**
 * @author Artur Bosch
 */
class DetektRulesDefinition : RulesDefinition {

	override fun define(context: RulesDefinition.Context) {
		context.createRepository(DETEKT_REPOSITORY, KOTLIN_KEY)
				.setName(DETEKT_ANALYZER)
				.createRules()
				.done()
	}

}