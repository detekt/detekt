package io.gitlab.arturbosch.detekt.core.v2.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.v2.Issue
import io.gitlab.arturbosch.detekt.api.v2.ResolvedContext
import io.gitlab.arturbosch.detekt.api.v2.Rule
import io.gitlab.arturbosch.detekt.api.v2.providers.CollectionRuleProvider
import io.gitlab.arturbosch.detekt.core.v2.Filter
import io.gitlab.arturbosch.detekt.core.v2.reusable
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.nio.file.Path
import java.util.ServiceLoader

interface RulesProvider {
    suspend fun get(resolvedContext: Deferred<ResolvedContext>): Flow<Pair<Rule, Filter>>
}

@OptIn(FlowPreview::class)
class RulesProviderImpl(
    private val config: Config,
    private val collectionRuleProviders: Flow<CollectionRuleProvider>,
) : RulesProvider {

    constructor(
        pluginLoader: ClassLoader,
        config: Config,
    ) : this(
        config,
        flow { emitAll(ServiceLoader.load(CollectionRuleProvider::class.java, pluginLoader).asFlow()) },
    )

    override suspend fun get(resolvedContext: Deferred<ResolvedContext>): Flow<Pair<Rule, Filter>> {
        return collectionRuleProviders
            .flatMapMerge { collectionProvider -> collectionProvider.get(config, resolvedContext) }
            .map { rule ->
                rule to object : Filter { // temporary meanwhile we find the place to instantiate this
                    override fun filter(path: Path): Boolean {
                        TODO("Not yet implemented")
                    }

                    override fun filter(issue: Issue): Boolean {
                        TODO("Not yet implemented")
                    }
                }
            }
            .reusable(UNLIMITED)
    }
}
