package io.gitlab.arturbosch.detekt.test

import io.gitlab.arturbosch.detekt.api.CompilerResources
import org.jetbrains.kotlin.config.AnalysisFlags
import org.jetbrains.kotlin.config.ApiVersion
import org.jetbrains.kotlin.config.ExplicitApiMode
import org.jetbrains.kotlin.config.LanguageVersion
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl

@Suppress("FunctionName")
fun FakeCompilerResources(
    mode: ExplicitApiMode = ExplicitApiMode.DISABLED,
): CompilerResources {
    val languageVersionSettings = LanguageVersionSettingsImpl(
        languageVersion = LanguageVersion.LATEST_STABLE,
        apiVersion = ApiVersion.LATEST_STABLE,
        analysisFlags = mapOf(AnalysisFlags.explicitApiMode to mode),
        specificFeatures = emptyMap(),
    )
    return CompilerResources(
        languageVersionSettings
    )
}
