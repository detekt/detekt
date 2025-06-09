package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.config.LanguageVersionSettings

/**
 * Provides compiler resources.
 */
class CompilerResources(
    val languageVersionSettings: LanguageVersionSettings,
)
