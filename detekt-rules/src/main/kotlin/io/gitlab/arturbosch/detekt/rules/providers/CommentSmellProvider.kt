package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.documentation.CommentOverPrivateMethod
import io.gitlab.arturbosch.detekt.rules.documentation.CommentOverPrivateProperty
import io.gitlab.arturbosch.detekt.rules.documentation.UndocumentedPublicClass
import io.gitlab.arturbosch.detekt.rules.documentation.UndocumentedPublicFunction

/**
 * @author Artur Bosch
 */
class CommentSmellProvider : RuleSetProvider {

	override val ruleSetId: String = "comments"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				CommentOverPrivateMethod(config),
				CommentOverPrivateProperty(config),
				UndocumentedPublicClass(config),
				UndocumentedPublicFunction(config)
		))
	}

}