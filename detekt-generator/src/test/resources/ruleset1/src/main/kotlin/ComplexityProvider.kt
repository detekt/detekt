package ruleset1

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * Description
 */
@ActiveByDefault(since = "1.0.0")
class ComplexityProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "complexity"

    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::CognitiveComplexMethod,
        )
    )
}
