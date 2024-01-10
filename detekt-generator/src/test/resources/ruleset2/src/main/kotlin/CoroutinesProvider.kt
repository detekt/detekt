package ruleset2

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * Description
 */
@ActiveByDefault(since = "1.4.0")
class CoroutinesProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "coroutines"

    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::GlobalCoroutineUsage,
        )
    )
}
