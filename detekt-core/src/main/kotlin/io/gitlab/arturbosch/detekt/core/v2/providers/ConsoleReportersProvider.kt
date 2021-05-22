package io.gitlab.arturbosch.detekt.core.v2.providers

import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.v2.ConsoleReporter
import io.gitlab.arturbosch.detekt.api.v2.providers.CollectionConsoleReporterProvider
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import java.util.ServiceLoader

fun interface ConsoleReportersProvider {
    fun get(): Flow<ConsoleReporter>
}

@OptIn(FlowPreview::class, UnstableApi::class)
class ConsoleReportersProviderImpl(
    private val collectionConsoleReporterProviders: Flow<CollectionConsoleReporterProvider>,
    private val setupContext: SetupContext,
) : ConsoleReportersProvider {

    constructor(
        pluginLoader: ClassLoader,
        setupContext: SetupContext,
    ) : this(
        flow {
            emitAll(
                ServiceLoader.load(CollectionConsoleReporterProvider::class.java, pluginLoader).asFlow()
            )
        },
        setupContext,
    )

    override fun get(): Flow<ConsoleReporter> {
        return collectionConsoleReporterProviders
            .flatMapMerge { collectionProvider -> collectionProvider.get(setupContext) }
    }
}
