package io.gitlab.arturbosch.detekt.api.v2.providers

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.v2.ResolvedContext
import io.gitlab.arturbosch.detekt.api.v2.Rule
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

fun interface CollectionRuleProvider {
    fun get(
        config: Config, // Probably we want to rethink this class
        resolvedContext: Deferred<ResolvedContext>,
    ): Flow<Rule>
}
