package io.gitlab.arturbosch.detekt.authors

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault

/**
 * The rule authors ruleset provides rules that ensures good practices when writing detekt rules
 */
@ActiveByDefault("1.22.0")
class RuleAuthorsProvider : RuleSetProvider {

    override val ruleSetId: String = "ruleauthors"

    override fun instance(config: Config) = RuleSet(
        ruleSetId,
        listOf(
            RequiresTypeResolution(config),
        )
    )
}
