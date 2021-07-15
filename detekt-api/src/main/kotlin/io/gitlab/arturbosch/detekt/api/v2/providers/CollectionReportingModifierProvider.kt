package io.gitlab.arturbosch.detekt.api.v2.providers

import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.v2.ReportingModifier
import kotlinx.coroutines.flow.Flow

fun interface CollectionReportingModifierProvider {
    fun get(@OptIn(UnstableApi::class) setupContext: SetupContext): Flow<ReportingModifier>
}
