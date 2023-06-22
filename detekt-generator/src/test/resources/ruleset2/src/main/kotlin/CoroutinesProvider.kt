package ruleset2

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.DefaultRuleSetProvider

/**
 * Description
 */
@ActiveByDefault(since = "1.4.0")
class CoroutinesProvider : DefaultRuleSetProvider {

    override val ruleSetId: String = "coroutines"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(
            GlobalCoroutineUsage(config),
        )
    )
}
