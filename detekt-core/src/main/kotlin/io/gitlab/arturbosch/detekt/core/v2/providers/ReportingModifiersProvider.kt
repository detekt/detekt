package io.gitlab.arturbosch.detekt.core.v2.providers

import io.gitlab.arturbosch.detekt.api.v2.ReportingModifier
import io.gitlab.arturbosch.detekt.api.v2.providers.CollectionReportingModifierProvider
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

@OptIn(FlowPreview::class)
class ReportingModifiersProviderImpl(
    private val collectionReportingModifierProviders: Flow<CollectionReportingModifierProvider>,
) : ReportingModifiersProvider {

    constructor(
        pluginLoader: ClassLoader,
    ) : this(
        flow {
            emitAll(
                ServiceLoader.load(CollectionReportingModifierProvider::class.java, pluginLoader).asFlow()
            )
        }
    )

    override fun get(): Flow<ReportingModifier> {
        return collectionReportingModifierProviders
            .flatMapMerge { collectionProvider -> collectionProvider.get() }
        // TODO I think that we need to sort this list. I'll check it later
    }
}
