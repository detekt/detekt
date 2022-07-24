package io.gitlab.arturbosch.detekt.authors

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault

/**
 * The authors ruleset provides rules that ensures good practices when writing detekt rules
 */
@ActiveByDefault("1.22.0")
class AuthorsProvider : RuleSetProvider {

    override val ruleSetId: String = "detekt"

    override fun instance(config: Config) = RuleSet(
        ruleSetId,
        listOf(
            RequiresTypeResolutionRulesDoesNotRunWithoutAContext(config),
        )
    )
}
