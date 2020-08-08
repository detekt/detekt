package io.github.detekt.custom

import io.github.detekt.custom.CustomChecksProvider.Companion.RULE_SET_NAME
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConfigValidator
import io.gitlab.arturbosch.detekt.api.Notification
import io.gitlab.arturbosch.detekt.api.internal.SimpleNotification

class CustomChecksConfigValidator : ConfigValidator {

    override fun validate(config: Config): Collection<Notification> {
        val result = mutableListOf<Notification>()
        val customRules = config.subConfig(RULE_SET_NAME)

        fun validateSpekTestDiscoveryConfig() {
            val rule = SpekTestDiscovery::class.java.simpleName
            val spekRule = customRules.subConfig(rule)
            spekRule.valueOrNull<List<String>>(SpekTestDiscovery.SCOPING_FUNCTIONS)
                ?.filter { it.contains(".") }
                ?.forEach {
                    result.add(
                        SimpleNotification("$rule>${SpekTestDiscovery.SCOPING_FUNCTIONS}: $it must not be qualified.")
                    )
                }
            spekRule.valueOrNull<List<String>>(SpekTestDiscovery.ALLOWED_TYPES)
                ?.filterNot { it.contains(".") }
                ?.forEach {
                    result.add(
                        SimpleNotification("$rule>${SpekTestDiscovery.ALLOWED_TYPES}: $it must be qualified.")
                    )
                }
        }

        validateSpekTestDiscoveryConfig()
        return result
    }
}
