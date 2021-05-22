package io.gitlab.arturbosch.detekt.core.v2

import io.gitlab.arturbosch.detekt.api.v2.Detektion
import io.gitlab.arturbosch.detekt.api.v2.FileProcessListener
import io.gitlab.arturbosch.detekt.api.v2.ResolvedContext
import io.gitlab.arturbosch.detekt.api.v2.Rule
import io.gitlab.arturbosch.detekt.core.v2.providers.ConsoleReportersProvider
import io.gitlab.arturbosch.detekt.core.v2.providers.FileProcessListenersProvider
import io.gitlab.arturbosch.detekt.core.v2.providers.KtFilesProvider
import io.gitlab.arturbosch.detekt.core.v2.providers.OutputReportersProvider
import io.gitlab.arturbosch.detekt.core.v2.providers.ReportingModifiersProvider
import io.gitlab.arturbosch.detekt.core.v2.providers.ResolvedContextProvider
import io.gitlab.arturbosch.detekt.core.v2.providers.RulesProvider
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import org.jetbrains.kotlin.psi.KtFile

suspend fun run(
    filesProvider: KtFilesProvider,
    resolvedContextProvider: ResolvedContextProvider,
    ruleProvider: RulesProvider,
    fileProcessListenersProvider: FileProcessListenersProvider,
    reportingModifiersProvider: ReportingModifiersProvider,
    consoleReportersProvider: ConsoleReportersProvider,
    outputReportersProvider: OutputReportersProvider,
): Detektion {
    return run(
        filesProvider,
        resolvedContextProvider,
        ruleProvider,
        fileProcessListenersProvider,
        reportingModifiersProvider,
        consoleReportersProvider,
        outputReportersProvider,
        ::analyze,
    )
}

internal suspend fun run(
    filesProvider: KtFilesProvider,
    resolvedContextProvider: ResolvedContextProvider,
    ruleProvider: RulesProvider,
    fileProcessListenersProvider: FileProcessListenersProvider,
    reportingModifiersProvider: ReportingModifiersProvider,
    consoleReportersProvider: ConsoleReportersProvider,
    outputReportersProvider: OutputReportersProvider,
    analyzer: suspend (Flow<Pair<Rule, Filter>>, Flow<KtFile>, Flow<FileProcessListener>) -> Detektion,
): Detektion {
    val detektion: Detektion = runAnalysis(
        filesProvider,
        resolvedContextProvider,
        ruleProvider,
        fileProcessListenersProvider,
        analyzer
    )
    val finalDetektion: Detektion = runPostAnalysis(detektion, reportingModifiersProvider)
    runReports(finalDetektion, consoleReportersProvider, outputReportersProvider)
    return finalDetektion
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
    val sortedReportingModifiers = reportingModifiers
        .toList()
        .sortedBy { it.priority }
    val finalDetektion = sortedReportingModifiers.fold(detektion) { acc: Detektion, reportingModifier ->
        reportingModifier.transform(acc)
    }
    sortedReportingModifiers.forEach { it.onFinalResult(detektion) }
    return finalDetektion
}

private suspend fun runReports(
    detektion: Detektion,
    consoleReportersProvider: ConsoleReportersProvider,
    outputReportersProvider: OutputReportersProvider,
) {
    // TODO These tow can of this can be parallel
    consoleReportersProvider.get()
        .toList()
        .sortedBy { it.priority }
        .forEach { it.render(detektion) }
    outputReportersProvider.get().collect { it.render(detektion) }
}
