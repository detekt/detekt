package io.gitlab.arturbosch.detekt.api.v2.providers

import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.v2.OutputReporter
import kotlinx.coroutines.flow.Flow

fun interface CollectionOutputReporterProvider {
    fun get(@OptIn(UnstableApi::class) setupContext: SetupContext): Flow<OutputReporter>
}
