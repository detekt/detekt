package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.rules.documentation.*

/**
 * This rule set provides rules that address issues in comments and documentation
 * of the code.
 *
 * @active since v1.0.0
 */
class CommentSmellProvider : RuleSetProvider {

    override val ruleSetId: String = "comments"

    override fun instance(config: Config): RuleSet {
        return RuleSet(ruleSetId, listOf(
                CommentOverPrivateFunction(config),
                CommentOverPrivateProperty(config),
                KDocStyle(config),
                UndocumentedPublicClass(config),
                UndocumentedPublicFunction(config),
                AbsentOrWrongFileLicense(config)
        ))
    }
}
