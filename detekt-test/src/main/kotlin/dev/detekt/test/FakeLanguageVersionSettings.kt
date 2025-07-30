package dev.detekt.test

import org.jetbrains.kotlin.config.AnalysisFlags
import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.ExplicitApiMode
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl

@Suppress("FunctionName")
fun FakeLanguageVersionSettings(
    mode: ExplicitApiMode = ExplicitApiMode.DISABLED,
): LanguageVersionSettingsImpl = LanguageVersionSettingsImpl(
    languageVersion = LanguageVersion.LATEST_STABLE,
    apiVersion = ApiVersion.LATEST_STABLE,
    analysisFlags = mapOf(AnalysisFlags.explicitApiMode to mode),
    specificFeatures = emptyMap(),
)
