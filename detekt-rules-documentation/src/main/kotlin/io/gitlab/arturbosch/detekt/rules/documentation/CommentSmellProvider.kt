package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * This rule set provides rules that address issues in comments and documentation
 * of the code.
 */
@ActiveByDefault(since = "1.0.0")
class CommentSmellProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "comments"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            CommentOverPrivateFunction(config),
            CommentOverPrivateProperty(config),
            KDocStyle(config),
            OutdatedDocumentation(config),
            UndocumentedPublicClass(config),
            UndocumentedPublicFunction(config),
            UndocumentedPublicProperty(config),
            AbsentOrWrongFileLicense(config),
            ReferencedEncapsulatedProperty(config)
        )
    )
}
