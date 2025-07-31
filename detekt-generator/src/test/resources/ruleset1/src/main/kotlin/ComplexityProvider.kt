package ruleset1

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.RuleSet
import dev.detekt.api.internal.DefaultRuleSetProvider

/**
 * Description
 */
@ActiveByDefault(since = "1.0.0")
class ComplexityProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSet.Id("complexity")

    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::CognitiveComplexMethod,
        )
    )
}
