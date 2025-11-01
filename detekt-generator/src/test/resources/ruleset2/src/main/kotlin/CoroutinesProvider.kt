package ruleset2

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.RuleSet
import dev.detekt.api.internal.DefaultRuleSetProvider

/**
 * Description
 */
@ActiveByDefault(since = "1.4.0")
class CoroutinesProvider : DefaultRuleSetProvider {

    override val ruleSetId = RuleSetId("coroutines")

    override fun instance(): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            ::GlobalCoroutineUsage,
        )
    )
}
