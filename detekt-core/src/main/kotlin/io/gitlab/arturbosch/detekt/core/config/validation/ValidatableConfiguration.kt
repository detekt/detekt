package io.gitlab.arturbosch.detekt.core.config.validation

import dev.detekt.api.Config
import dev.detekt.api.Notification

interface ValidatableConfiguration {
    fun validate(baseline: Config, excludePatterns: Set<Regex>): List<Notification>
}
