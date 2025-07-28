package io.gitlab.arturbosch.detekt.sample.extensions

import dev.detekt.api.Config
import dev.detekt.api.ConfigValidator
import dev.detekt.api.Notification

class SampleConfigValidator : ConfigValidator {

    override val id: String = "SampleConfigValidator"

    override fun validate(config: Config): Collection<Notification> {
        val result = mutableListOf<Notification>()
        runCatching {
            config.subConfig("sample")
                .subConfig("TooManyFunctions")
                .valueOrNull<Boolean>("active")
        }.onFailure {
            result.add(Notification("'active' property must be of type boolean.", Notification.Level.Error))
        }
        return result
    }
}
