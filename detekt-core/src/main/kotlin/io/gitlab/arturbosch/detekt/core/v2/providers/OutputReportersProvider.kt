package io.gitlab.arturbosch.detekt.core.v2.providers

import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.v2.OutputReporter
import io.gitlab.arturbosch.detekt.api.v2.providers.CollectionOutputReporterProvider
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import java.util.ServiceLoader

fun interface OutputReportersProvider {
    fun get(): Flow<OutputReporter>
}

@OptIn(FlowPreview::class, UnstableApi::class)
class OutputReportersProviderImpl(
    private val collectionOutputReporterProviders: Flow<CollectionOutputReporterProvider>,
    private val setupContext: SetupContext,
) : OutputReportersProvider {

    constructor(
        settings: ProcessingSettings
    ) : this(
        flow {
            emitAll(
                ServiceLoader.load(CollectionOutputReporterProvider::class.java, settings.pluginLoader).asFlow()
            )
        },
        settings,
    )

    override fun get(): Flow<OutputReporter> {
        return collectionOutputReporterProviders
            .flatMapMerge { collectionProvider -> collectionProvider.get(setupContext) }
    }
}
