package io.gitlab.arturbosch.detekt.cli

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.core.NotApiButProbablyUsedByUsers
import io.gitlab.arturbosch.detekt.core.config.DefaultConfig

@NotApiButProbablyUsedByUsers
const val DEFAULT_CONFIG: String = DefaultConfig.RESOURCE_NAME

@NotApiButProbablyUsedByUsers
fun loadDefaultConfig(): Config = DefaultConfig.newInstance()
