package io.gitlab.arturbosch.detekt.rules.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider
import io.gitlab.arturbosch.detekt.rules.documentation.AbsentOrWrongFileLicense
import io.gitlab.arturbosch.detekt.rules.documentation.CommentOverPrivateFunction
import io.gitlab.arturbosch.detekt.rules.documentation.CommentOverPrivateProperty
import io.gitlab.arturbosch.detekt.rules.documentation.KDocStyle
import io.gitlab.arturbosch.detekt.rules.documentation.UndocumentedPublicClass
import io.gitlab.arturbosch.detekt.rules.documentation.UndocumentedPublicFunction
import io.gitlab.arturbosch.detekt.rules.documentation.UndocumentedPublicProperty

/**
 * This rule set provides rules that address issues in comments and documentation
 * of the code.
 *
 * @active since v1.0.0
 */
class CommentSmellProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "comments"

    override fun instance(config: Config): RuleSet {
        return RuleSet(ruleSetId, listOf(
                CommentOverPrivateFunction(config),
                CommentOverPrivateProperty(config),
                KDocStyle(config),
                UndocumentedPublicClass(config),
                UndocumentedPublicFunction(config),
                UndocumentedPublicProperty(config),
                AbsentOrWrongFileLicense(config)
        ))
    }
}
