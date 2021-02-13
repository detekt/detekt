package io.gitlab.arturbosch.detekt.core.v2

import io.github.detekt.psi.absolutePath
import io.gitlab.arturbosch.detekt.api.internal.CompilerResources
import io.gitlab.arturbosch.detekt.api.v2.FileProcessListener
import io.gitlab.arturbosch.detekt.api.v2.Finding
import io.gitlab.arturbosch.detekt.api.v2.NewIssue
import io.gitlab.arturbosch.detekt.api.v2.PlainFileProcessListener
import io.gitlab.arturbosch.detekt.api.v2.PlainRule
import io.gitlab.arturbosch.detekt.api.v2.ResolvedContext
import io.gitlab.arturbosch.detekt.api.v2.Rule
import io.gitlab.arturbosch.detekt.api.v2.TypeSolvingFileProcessListener
import io.gitlab.arturbosch.detekt.api.v2.TypeSolvingRule
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

@FlowPreview
fun analyze(
    rules: Flow<Pair<Rule, Filter>>,
    files: Flow<KtFile>,
    fileProcessListeners: Flow<FileProcessListener>,
    bindingContextProvider: suspend (files: Flow<KtFile>) -> BindingContext,
    compilerResourcesProvider: suspend () -> CompilerResources,
): Flow<Finding> {
    return flow {
        coroutineScope {
            emitAll(
                myAnalyze(
                    rules.reusable(UNLIMITED),
                    files.reusable(UNLIMITED),
                    fileProcessListeners.reusable(UNLIMITED),
                    bindingContextProvider,
                    compilerResourcesProvider,
                )
            )
        }
    }
}

@FlowPreview
private fun myAnalyze(
    rules: Flow<Pair<Rule, Filter>>,
    files: Flow<KtFile>,
    fileProcessListeners: Flow<FileProcessListener>,
    bindingContextProvider: suspend (files: Flow<KtFile>) -> BindingContext,
    compilerResourcesProvider: suspend () -> CompilerResources,
): Flow<Finding> {
    return flow {
        val resolvedContext = buildResolvedContextAsync(files, bindingContextProvider, compilerResourcesProvider)

        fileProcessListeners.collect { listener -> listener.onStart(files, resolvedContext) }

        val findings = files
            .onEach { file ->
                fileProcessListeners.collect { listener ->
                    listener.onProcess(file, resolvedContext)
                }
            }
            .flatMapMerge { file ->
                flow {
                    val path = file.absolutePath()
                    val findings = rules
                        .filter { (_, filter) -> filter.filter(path) }
                        .flatMapMerge { (rule, filter) ->
                            rule.invoke(file, resolvedContext)
                                .filter { filter.filter(it) }
                                .map { it.toFinding(rule) }
                                .asFlow()
                        }
                        .toList()

                    emit(file to findings)
                }
            }
            .onEach { (file, findings) ->
                fileProcessListeners.collect { listener ->
                    listener.onProcessComplete(file, findings, resolvedContext)
                }
            }
            .flatMapMerge { (_, findings) -> findings.asFlow() }
            .reusable(UNLIMITED)


        fileProcessListeners.collect { listener ->
            listener.onFinish(files, findings.toList(), resolvedContext)
        }

        emitAll(findings)
    }
}

private suspend fun buildResolvedContextAsync(
    files: Flow<KtFile>,
    bindingContextProvider: suspend (files: Flow<KtFile>) -> BindingContext,
    compilerResourcesProvider: suspend () -> CompilerResources,
): Deferred<ResolvedContext> {
    class ResolvedContextImpl(
        override val binding: BindingContext,
        override val resources: CompilerResources
    ) : ResolvedContext

    return coroutineScope {
        async(start = CoroutineStart.LAZY) {
            ResolvedContextImpl(
                binding = bindingContextProvider.invoke(files),
                resources = compilerResourcesProvider.invoke()
            )
        }
    }
}

private suspend fun Rule.invoke(
    file: KtFile,
    resolvedContext: Deferred<ResolvedContext>
): List<NewIssue> {
    return when (this) {
        is TypeSolvingRule -> invoke(file, resolvedContext.await())
        is PlainRule -> invoke(file)
        else -> error("")
    }
}

private suspend fun FileProcessListener.onStart(
    files: Flow<KtFile>,
    resolvedContext: Deferred<ResolvedContext>
) {
    when (this) {
        is PlainFileProcessListener -> onStart(files.toList())
        is TypeSolvingFileProcessListener -> onStart(
            files.toList(),
            resolvedContext.await(),
        )
        else -> error("")
    }
}

private suspend fun FileProcessListener.onProcess(
    file: KtFile,
    resolvedContext: Deferred<ResolvedContext>
) {
    when (this) {
        is PlainFileProcessListener -> onProcess(file)
        is TypeSolvingFileProcessListener -> onProcess(
            file,
            resolvedContext.await(),
        )
        else -> error("")
    }
}

private suspend fun FileProcessListener.onProcessComplete(
    file: KtFile,
    findings: List<Finding>,
    resolvedContext: Deferred<ResolvedContext>
) {
    when (this) {
        is PlainFileProcessListener -> onProcessComplete(file, findings)
        is TypeSolvingFileProcessListener -> onProcessComplete(
            file,
            findings,
            resolvedContext.await(),
        )
        else -> error("")
    }
}

private suspend fun FileProcessListener.onFinish(
    files: Flow<KtFile>,
    findings: List<Finding>,
    resolvedContext: Deferred<ResolvedContext>
) {
    when (this) {
        is PlainFileProcessListener -> onFinish(
            files.toList(),
            findings
        )
        is TypeSolvingFileProcessListener -> onFinish(
            files.toList(),
            findings,
            resolvedContext.await(),
        )
        else -> error("")
    }
}
