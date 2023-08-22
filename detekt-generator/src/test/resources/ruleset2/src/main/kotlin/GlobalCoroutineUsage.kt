package ruleset2

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule

/**
 * Description
 */
class GlobalCoroutineUsage(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        "The usage of the `GlobalScope` instance is highly discouraged.",
        Debt.TEN_MINS
    )
}
