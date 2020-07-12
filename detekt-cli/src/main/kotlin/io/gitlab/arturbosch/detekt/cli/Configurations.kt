package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.tooling.api.DefaultConfigurationProvider
import io.github.detekt.tooling.internal.NotApiButProbablyUsedByUsers
import io.gitlab.arturbosch.detekt.api.Config

@NotApiButProbablyUsedByUsers
@Deprecated("""
    Exposes internal resource name. There should not be a case were just the resource name is needed.
    Please use the DefaultConfigurationProvider to get a default config instance.
"""
)
const val DEFAULT_CONFIG: String = "default-detekt-config.yml"

@NotApiButProbablyUsedByUsers
@Deprecated(
    "Use official api for the default config.",
    ReplaceWith(
        "DefaultConfigurationProvider.load().get()",
        "io.github.detekt.tooling.api.DefaultConfigurationProvider"
    )
)
fun loadDefaultConfig(): Config = DefaultConfigurationProvider.load().get()
