package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * This rule set provides rules that address issues in comments and documentation
 * of the code.
 */
@ActiveByDefault(since = "1.0.0")
class CommentSmellProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSet.Id("comments")

    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::CommentOverPrivateFunction,
            ::CommentOverPrivateProperty,
            ::DeprecatedBlockTag,
            ::EndOfSentenceFormat,
            ::OutdatedDocumentation,
            ::UndocumentedPublicClass,
            ::UndocumentedPublicFunction,
            ::UndocumentedPublicProperty,
            ::AbsentOrWrongFileLicense,
            ::KDocReferencesNonPublicProperty
        )
    )
}
