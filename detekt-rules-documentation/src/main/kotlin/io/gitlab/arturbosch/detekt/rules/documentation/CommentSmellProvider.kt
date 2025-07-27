package io.gitlab.arturbosch.detekt.rules.documentation

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.RuleSet
import dev.detekt.api.internal.DefaultRuleSetProvider

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
