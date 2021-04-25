package io.gitlab.arturbosch.detekt.core.v2.providers

import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.v2.FileProcessListener
import io.gitlab.arturbosch.detekt.api.v2.ResolvedContext
import io.gitlab.arturbosch.detekt.api.v2.providers.CollectionFileProcessListenerProvider
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.v2.reusable
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import java.util.ServiceLoader

interface FileProcessListenersProvider {
    suspend fun get(resolvedContext: Deferred<ResolvedContext>): Flow<FileProcessListener>
}

@OptIn(FlowPreview::class, UnstableApi::class)
class FileProcessListenersProviderImpl(
    private val collectionFileProcessListenerProviders: Flow<CollectionFileProcessListenerProvider>,
    private val setupContext: SetupContext,
) : FileProcessListenersProvider {

    constructor(
        settings: ProcessingSettings
    ) : this(
        flow {
            emitAll(
                ServiceLoader.load(CollectionFileProcessListenerProvider::class.java, settings.pluginLoader).asFlow()
            )
        },
        settings,
    )

    override suspend fun get(resolvedContext: Deferred<ResolvedContext>): Flow<FileProcessListener> {
        return collectionFileProcessListenerProviders
            .flatMapMerge { collectionProvider -> collectionProvider.get(setupContext, resolvedContext) }
            .reusable(UNLIMITED)
    }
}
