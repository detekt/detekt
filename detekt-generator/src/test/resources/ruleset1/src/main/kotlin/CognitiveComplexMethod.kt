package ruleset1

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * Description
 */
class CognitiveComplexMethod(config: Config = Config.empty) : Rule(config, "Prefer splitting up complex methods into smaller, easier to understand methods.") {

    @Configuration("Maximum Cognitive Complexity allowed for a method.")
    private val allowedComplexity: Int by config(defaultValue = 15)

    @SuppressWarnings("EmptyFunctionBlock")
    override fun visitNamedFunction(function: KtNamedFunction) {
        // This is just for testing purpose
    }
}
