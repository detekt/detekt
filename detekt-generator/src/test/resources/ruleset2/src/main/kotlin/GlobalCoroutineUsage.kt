package ruleset2

import dev.detekt.api.Config
import dev.detekt.api.Rule

/**
 * Description
 */
class GlobalCoroutineUsage(config: Config = Config.empty) : Rule(config, "The usage of the `GlobalScope` instance is highly discouraged.") {
}
