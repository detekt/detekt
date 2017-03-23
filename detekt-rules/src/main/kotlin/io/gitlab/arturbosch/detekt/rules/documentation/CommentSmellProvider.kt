package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.documentation.CommentOverPrivateMethod
import io.gitlab.arturbosch.detekt.rules.documentation.CommentOverPrivateProperty
import io.gitlab.arturbosch.detekt.rules.documentation.NoDocOverPublicClass
import io.gitlab.arturbosch.detekt.rules.documentation.NoDocOverPublicMethod

/**
 * @author Artur Bosch
 */
class CommentSmellProvider : RuleSetProvider {

	override val ruleSetId: String = "comments"

	override fun instance(config: Config): RuleSet {
		return RuleSet(ruleSetId, listOf(
				CommentOverPrivateMethod(config),
				CommentOverPrivateProperty(config),
				NoDocOverPublicClass(config),
				NoDocOverPublicMethod(config)
		))
	}

}