package io.gitlab.arturbosch.detekt.core.config.validation

internal data class ValidationSettings(
    val warningsAsErrors: Boolean = false,
    val checkExhaustiveness: Boolean = false,
    val excludePatterns: Set<Regex> = emptySet(),
)
