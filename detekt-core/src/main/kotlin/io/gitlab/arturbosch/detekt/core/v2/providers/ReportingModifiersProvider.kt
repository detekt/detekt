package io.gitlab.arturbosch.detekt.core.v2.providers

import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.v2.ReportingModifier
import io.gitlab.arturbosch.detekt.api.v2.providers.CollectionReportingModifierProvider
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import java.util.ServiceLoader

fun interface ReportingModifiersProvider {
    fun get(): Flow<ReportingModifier>
}

@OptIn(FlowPreview::class, UnstableApi::class)
class ReportingModifiersProviderImpl(
    private val collectionReportingModifierProviders: Flow<CollectionReportingModifierProvider>,
    private val setupContext: SetupContext,
) : ReportingModifiersProvider {

    constructor(
        settings: ProcessingSettings
    ) : this(
        flow {
            emitAll(
                ServiceLoader.load(CollectionReportingModifierProvider::class.java, settings.pluginLoader).asFlow()
            )
        },
        settings,
    )

    override fun get(): Flow<ReportingModifier> {
        return collectionReportingModifierProviders
            .flatMapMerge { collectionProvider -> collectionProvider.get(setupContext) }
    }
}
