package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.CommentOverPrivateMethod
import io.gitlab.arturbosch.detekt.rules.CommentOverPrivateProperty
import io.gitlab.arturbosch.detekt.rules.NoDocOverPublicClass
import io.gitlab.arturbosch.detekt.rules.NoDocOverPublicMethod

/**
 * @author Artur Bosch
 */
class CommentSmellProvider : RuleSetProvider {

	override fun instance(config: Config): RuleSet {
		return RuleSet("comments", listOf(
				CommentOverPrivateMethod(config),
				CommentOverPrivateProperty(config),
				NoDocOverPublicClass(config),
				NoDocOverPublicMethod(config)
		))
	}

}