package io.gitlab.arturbosch.detekt.api.v2.providers

import io.gitlab.arturbosch.detekt.api.v2.FileProcessListener
import io.gitlab.arturbosch.detekt.api.v2.ResolvedContext
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

fun interface CollectionFileProcessListenerProvider {
    fun get(
        resolvedContext: Deferred<ResolvedContext>,
    ): Flow<FileProcessListener>
}
