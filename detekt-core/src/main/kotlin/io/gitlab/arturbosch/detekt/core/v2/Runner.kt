package io.gitlab.arturbosch.detekt.core.v2

import io.github.detekt.tooling.api.AnalysisResult
import io.github.detekt.tooling.api.Detekt
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.api.v2.Detektion
import io.gitlab.arturbosch.detekt.api.v2.FileProcessListener
import io.gitlab.arturbosch.detekt.api.v2.ResolvedContext
import io.gitlab.arturbosch.detekt.api.v2.Rule
import io.gitlab.arturbosch.detekt.core.tooling.contentToKtFile
import io.gitlab.arturbosch.detekt.core.tooling.pathToKtFile
import io.gitlab.arturbosch.detekt.core.tooling.withSettings
import io.gitlab.arturbosch.detekt.core.v2.providers.FileProcessListenersProvider
import io.gitlab.arturbosch.detekt.core.v2.providers.FileProcessListenersProviderImpl
import io.gitlab.arturbosch.detekt.core.v2.providers.KtFilesProvider
import io.gitlab.arturbosch.detekt.core.v2.providers.KtFilesProviderImpl
import io.gitlab.arturbosch.detekt.core.v2.providers.ResolvedContextProvider
import io.gitlab.arturbosch.detekt.core.v2.providers.ResolvedContextProviderImpl
import io.gitlab.arturbosch.detekt.core.v2.providers.ResolvedContextProviderWithBindingContext
import io.gitlab.arturbosch.detekt.core.v2.providers.RulesProvider
import io.gitlab.arturbosch.detekt.core.v2.providers.RulesProviderImpl
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import java.nio.file.Path
import java.nio.file.Paths

class Runner(
    private val spec: ProcessingSpec // This class probably needs a refactor too
) : Detekt { // TODO all of this should be coroutines
    override fun run(): AnalysisResult {
        return spec.withSettings {
            runBlocking {
                run(
                    KtFilesProviderImpl(this@withSettings),
                    ResolvedContextProviderImpl(environment, classpath),
                    RulesProviderImpl(this@withSettings),
                    FileProcessListenersProviderImpl(this@withSettings),
                    ::analyze
                )
            }
        }
    }

    override fun run(path: Path): AnalysisResult {
        return spec.withSettings {
            runBlocking {
                run(
                    { pathToKtFile(path).invoke(this@withSettings).asFlow() },
                    ResolvedContextProviderImpl(environment, classpath),
                    RulesProviderImpl(this@withSettings),
                    FileProcessListenersProviderImpl(this@withSettings),
                    ::analyze
                )
            }
        }
    }

    override fun run(sourceCode: String, filename: String): AnalysisResult {
        return spec.withSettings {
            runBlocking {
                run(
                    { contentToKtFile(sourceCode, Paths.get(filename)).invoke(this@withSettings).asFlow() },
                    ResolvedContextProviderImpl(environment, classpath),
                    RulesProviderImpl(this@withSettings),
                    FileProcessListenersProviderImpl(this@withSettings),
                    ::analyze
                )
            }
        }
    }

    override fun run(files: Collection<KtFile>, bindingContext: BindingContext): AnalysisResult { // TODO Flow<KtFile>
        return spec.withSettings {
            runBlocking {
                run(
                    files::asFlow,
                    ResolvedContextProviderWithBindingContext(
                        bindingContext,
                        environment.configuration.languageVersionSettings
                    ),
                    RulesProviderImpl(this@withSettings),
                    FileProcessListenersProviderImpl(this@withSettings),
                    ::analyze
                )
            }
        }
    }
}

private suspend fun run(
    filesProvider: KtFilesProvider,
    resolvedContextProvider: ResolvedContextProvider,
    ruleProvider: RulesProvider,
    fileProcessListenersProvider: FileProcessListenersProvider,
    analyzer: suspend (Flow<Pair<Rule, Filter>>, Flow<KtFile>, Flow<FileProcessListener>) -> Detektion,
): AnalysisResult {
    val files: Flow<KtFile> = filesProvider.get()
    val resolvedContext: Deferred<ResolvedContext> = coroutineScope {
        async(start = CoroutineStart.LAZY) { resolvedContextProvider.get(files) }
    }
    val rules: Flow<Pair<Rule, Filter>> = ruleProvider.get(resolvedContext)
    val fileProcessListeners: Flow<FileProcessListener> = fileProcessListenersProvider.get(resolvedContext)
    val detektion: Detektion = analyzer(rules, files, fileProcessListeners)
    TODO("Not yet implemented")
}
