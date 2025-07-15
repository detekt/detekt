package io.gitlab.arturbosch.detekt.sample.extensions

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.ConfigValidator
import io.gitlab.arturbosch.detekt.api.Notification

class SampleConfigValidator : ConfigValidator {

    override val id: String = "SampleConfigValidator"

    override fun validate(config: Config): Collection<Notification> {
        val result = mutableListOf<Notification>()
        runCatching {
            config.subConfig("sample")
                .subConfig("TooManyFunctions")
                .valueOrNull<Boolean>("active")
        }.onFailure {
            result.add(SampleMessage("'active' property must be of type boolean."))
        }
        return result
    }
}

class SampleMessage(
    override val message: String,
    override val level: Notification.Level = Notification.Level.Error,
) : Notification
