package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory

/**
 * Provides compiler resources.
 */
class CompilerResources(
    val languageVersionSettings: LanguageVersionSettings,
    val dataFlowValueFactory: DataFlowValueFactory,
)
