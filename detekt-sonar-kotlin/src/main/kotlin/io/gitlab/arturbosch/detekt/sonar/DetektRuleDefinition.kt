package io.gitlab.arturbosch.detekt.sonar

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