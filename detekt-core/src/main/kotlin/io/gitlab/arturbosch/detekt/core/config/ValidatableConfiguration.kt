package io.gitlab.arturbosch.detekt.core.config

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Notification

interface ValidatableConfiguration {
    fun validate(baseline: Config, excludePatterns: Set<Regex>): List<Notification>
}
