package ruleset2

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Rule

/**
 * Description
 */
class GlobalCoroutineUsage(config: Config = Config.empty) : Rule(config, "The usage of the `GlobalScope` instance is highly discouraged.") {
}
