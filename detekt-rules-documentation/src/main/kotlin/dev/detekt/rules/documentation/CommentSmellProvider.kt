package dev.detekt.rules.documentation

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.internal.DefaultRuleSetProvider

/**
 * This rule set provides rules that address issues in comments and documentation
 * of the code.
 */
@ActiveByDefault(since = "1.0.0")
class CommentSmellProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSetId("comments")

    override fun instance(): RuleSet =
        RuleSet(
            ruleSetId,
            listOf(
                ::DocumentationOverPrivateFunction,
                ::DocumentationOverPrivateProperty,
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
