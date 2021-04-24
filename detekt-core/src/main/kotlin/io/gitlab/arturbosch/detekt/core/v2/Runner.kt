package io.gitlab.arturbosch.detekt.core.v2

import io.github.detekt.tooling.api.AnalysisResult
import io.github.detekt.tooling.api.Detekt
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.api.v2.Detektion
import io.gitlab.arturbosch.detekt.api.v2.FileProcessListener
import io.gitlab.arturbosch.detekt.api.v2.ResolvedContext
import io.gitlab.arturbosch.detekt.api.v2.Rule
import io.gitlab.arturbosch.detekt.core.ProcessingSettings
import io.gitlab.arturbosch.detekt.core.tooling.contentToKtFile
import io.gitlab.arturbosch.detekt.core.tooling.pathToKtFile
import io.gitlab.arturbosch.detekt.core.tooling.withSettings
import io.gitlab.arturbosch.detekt.core.v2.providers.ConsoleReportersProvider
import io.gitlab.arturbosch.detekt.core.v2.providers.ConsoleReportersProviderImpl
import io.gitlab.arturbosch.detekt.core.v2.providers.FileProcessListenersProvider
import io.gitlab.arturbosch.detekt.core.v2.providers.FileProcessListenersProviderImpl
import io.gitlab.arturbosch.detekt.core.v2.providers.KtFilesProvider
import io.gitlab.arturbosch.detekt.core.v2.providers.KtFilesProviderImpl
import io.gitlab.arturbosch.detekt.core.v2.providers.OutputReportersProvider
import io.gitlab.arturbosch.detekt.core.v2.providers.OutputReportersProviderImpl
import io.gitlab.arturbosch.detekt.core.v2.providers.ReportingModifiersProvider
import io.gitlab.arturbosch.detekt.core.v2.providers.ReportingModifiersProviderImpl
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.fold
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
                )
            }
        }
    }
}

private suspend fun ProcessingSettings.run(
    filesProvider: KtFilesProvider,
    resolvedContextProvider: ResolvedContextProvider,
    ruleProvider: RulesProvider,
): AnalysisResult {
    return run(
        filesProvider,
        resolvedContextProvider,
        ruleProvider,
        FileProcessListenersProviderImpl(pluginLoader),
        ReportingModifiersProviderImpl(pluginLoader),
        ConsoleReportersProviderImpl(pluginLoader),
        OutputReportersProviderImpl(pluginLoader),
        ::analyze,
    )
}

private suspend fun run(
    filesProvider: KtFilesProvider,
    resolvedContextProvider: ResolvedContextProvider,
    ruleProvider: RulesProvider,
    fileProcessListenersProvider: FileProcessListenersProvider,
    reportingModifiersProvider: ReportingModifiersProvider,
    consoleReportersProvider: ConsoleReportersProvider,
    outputReportersProvider: OutputReportersProvider,
    analyzer: suspend (Flow<Pair<Rule, Filter>>, Flow<KtFile>, Flow<FileProcessListener>) -> Detektion,
): AnalysisResult {
    val detektion: Detektion = runAnalysis(
        filesProvider,
        resolvedContextProvider,
        ruleProvider,
        fileProcessListenersProvider,
        analyzer
    )
    val finalDetektion: Detektion = runPostAnalysis(detektion, reportingModifiersProvider)
    runReports(finalDetektion, consoleReportersProvider, outputReportersProvider)
    TODO("Not yet implemented")
}

private suspend fun runAnalysis(
    filesProvider: KtFilesProvider,
    resolvedContextProvider: ResolvedContextProvider,
    ruleProvider: RulesProvider,
    fileProcessListenersProvider: FileProcessListenersProvider,
    analyzer: suspend (Flow<Pair<Rule, Filter>>, Flow<KtFile>, Flow<FileProcessListener>) -> Detektion,
): Detektion {
    val files: Flow<KtFile> = filesProvider.get()
    val resolvedContext: Deferred<ResolvedContext> = coroutineScope {
        async(start = CoroutineStart.LAZY) { resolvedContextProvider.get(files) }
    }
    val rules: Flow<Pair<Rule, Filter>> = ruleProvider.get(resolvedContext)
    val fileProcessListeners: Flow<FileProcessListener> = fileProcessListenersProvider.get(resolvedContext)
    return analyzer(rules, files, fileProcessListeners)
}

private suspend fun runPostAnalysis(
    detektion: Detektion,
    reportingModifiersProvider: ReportingModifiersProvider,
): Detektion {
    val reportingModifiers = reportingModifiersProvider.get()
    reportingModifiers.collect { it.onRawResult(detektion) }
    val finalDetektion = reportingModifiers.fold(detektion) { acc: Detektion, reportingModifier ->
        reportingModifier.transform(acc)
    }
    reportingModifiers.collect { it.onFinalResult(detektion) }
    return finalDetektion
}

private suspend fun runReports(
    detektion: Detektion,
    consoleReportersProvider: ConsoleReportersProvider,
    outputReportersProvider: OutputReportersProvider,
) {
    // TODO all of this can be parallel BUT console between them should be serial
    consoleReportersProvider.get().collect { it.render(detektion) }
    outputReportersProvider.get().collect { it.render(detektion) }
}
