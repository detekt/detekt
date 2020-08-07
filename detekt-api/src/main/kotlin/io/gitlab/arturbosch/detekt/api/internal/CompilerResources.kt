package io.gitlab.arturbosch.detekt.api.internal

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory

/**
 * Provides compiler resources.
 */
data class CompilerResources(
    val languageVersionSettings: LanguageVersionSettings,
    val dataFlowValueFactory: DataFlowValueFactory
)
