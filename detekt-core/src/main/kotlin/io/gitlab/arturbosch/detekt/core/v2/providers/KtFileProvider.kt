package io.gitlab.arturbosch.detekt.core.v2.providers

import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.tooling.inputPathsToKtFiles
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import org.jetbrains.kotlin.psi.KtFile

fun interface KtFilesProvider {
    fun get(): Flow<KtFile>
}

class KtFileProviderImpl(
    private val settings: ProcessingSettings,
) : KtFilesProvider {
    override fun get(): Flow<KtFile> {
        return inputPathsToKtFiles.invoke(settings).asFlow()
    }
}
