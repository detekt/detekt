package io.gitlab.arturbosch.detekt.core.v2.providers

import io.gitlab.arturbosch.detekt.api.internal.CompilerResources
import io.gitlab.arturbosch.detekt.api.v2.ResolvedContext
import io.gitlab.arturbosch.detekt.core.generateBindingContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactoryImpl


interface ResolvedContextProvider {
    suspend fun get(files: Flow<KtFile>): ResolvedContext
}

class ResolvedContextProviderImpl(
    private val environment: KotlinCoreEnvironment,
    private val classpath: List<String>,
) : ResolvedContextProvider {
    override suspend fun get(files: Flow<KtFile>): ResolvedContext {
        val binding = generateBindingContext(environment, classpath, files.toList())
        return object : ResolvedContext {
            override val binding = binding.takeIf { it != BindingContext.EMPTY }
            override val resources = compilerResources(environment.configuration.languageVersionSettings)
        }
    }
}

class ResolvedContextProviderWithBindingContext(
    private val bindingContext: BindingContext?,
    private val languageVersionSettings: LanguageVersionSettings,
) : ResolvedContextProvider {
    override suspend fun get(files: Flow<KtFile>): ResolvedContext {
        return object : ResolvedContext {
            override val binding = bindingContext
            override val resources = compilerResources(languageVersionSettings)
        }
    }
}

private fun compilerResources(languageVersionSettings: LanguageVersionSettings): CompilerResources {
    @Suppress("DEPRECATION")
    val dataFlowValueFactory = DataFlowValueFactoryImpl(languageVersionSettings)
    return CompilerResources(languageVersionSettings, dataFlowValueFactory)
}
