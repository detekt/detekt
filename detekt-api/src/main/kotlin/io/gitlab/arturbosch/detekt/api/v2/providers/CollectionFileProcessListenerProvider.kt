package io.gitlab.arturbosch.detekt.api.v2.providers

import io.gitlab.arturbosch.detekt.api.SetupContext
import io.gitlab.arturbosch.detekt.api.UnstableApi
import io.gitlab.arturbosch.detekt.api.v2.FileProcessListener
import io.gitlab.arturbosch.detekt.api.v2.ResolvedContext
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

fun interface CollectionFileProcessListenerProvider {
    fun get(
        @OptIn(UnstableApi::class) setupContext: SetupContext,
        resolvedContext: Deferred<ResolvedContext>,
    ): Flow<FileProcessListener>
}
