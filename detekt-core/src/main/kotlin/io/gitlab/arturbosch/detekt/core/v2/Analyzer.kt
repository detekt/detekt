package io.gitlab.arturbosch.detekt.core.v2

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.internal.CompilerResources
import io.gitlab.arturbosch.detekt.api.v2.FileProcessListener
import io.gitlab.arturbosch.detekt.api.v2.NewIssue
import io.gitlab.arturbosch.detekt.api.v2.PlainFileProcessListener
import io.gitlab.arturbosch.detekt.api.v2.PlainRule
import io.gitlab.arturbosch.detekt.api.v2.Rule
import io.gitlab.arturbosch.detekt.api.v2.TypeSolvingFileProcessListener
import io.gitlab.arturbosch.detekt.api.v2.TypeSolvingRule
import io.gitlab.arturbosch.detekt.core.DetektResult
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
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

@FlowPreview
fun analyze(
    rules: Flow<Rule>,
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
                    async(start = CoroutineStart.LAZY) { bindingContextProvider(files) },
                    async(start = CoroutineStart.LAZY) { compilerResourcesProvider() },
                )
            )
        }
    }
}

@FlowPreview
private fun myAnalyze(
    rules: Flow<Rule>,
    files: Flow<KtFile>,
    fileProcessListeners: Flow<FileProcessListener>,
    bindingContext: Deferred<BindingContext>,
    compilerResources: Deferred<CompilerResources>,
): Flow<Finding> {
    return flow {
        fileProcessListeners.collect { listener -> listener.onStart(files, bindingContext, compilerResources) }

        val findings = files
            .onEach { file ->
                fileProcessListeners.collect { listener ->
                    listener.onProcess(file, bindingContext, compilerResources)
                }
            }
            .flatMapMerge { file ->
                flow {
                    val findings = rules
                        // TODO check if we should filter this rule on this file
                        .flatMapMerge { rule ->
                            rule.invoke(file, bindingContext, compilerResources)
                                .map<NewIssue, Finding> { CodeSmell(rule.issue, it.entity, it.message) }
                                // TODO check if we should filter this issue (Suppress, ignore...)
                                .asFlow()
                        }
                        .toList()

                    emit(file to findings)
                }
            }
            .onEach { (file, findings) ->
                fileProcessListeners.collect { listener ->
                    listener.onProcessComplete(file, findings, bindingContext, compilerResources)
                }
            }
            .flatMapMerge { (_, findings) -> findings.asFlow() }
            .reusable(UNLIMITED)


        fileProcessListeners.collect { listener ->
            listener.onFinish(files, DetektResult(mapOf("a" to findings.toList())), bindingContext, compilerResources)
        }

        emitAll(findings)
    }
}

private suspend fun Rule.invoke(
    file: KtFile,
    bindingContext: Deferred<BindingContext>,
    compilerResources: Deferred<CompilerResources>
): List<NewIssue> {
    return when (this) {
        is TypeSolvingRule -> invoke(file, bindingContext.await(), compilerResources.await())
        is PlainRule -> invoke(file)
        else -> error("")
    }
}

private suspend fun FileProcessListener.onStart(
    files: Flow<KtFile>,
    bindingContext: Deferred<BindingContext>,
    compilerResources: Deferred<CompilerResources>
) {
    when (this) {
        is PlainFileProcessListener -> onStart(files.toList())
        is TypeSolvingFileProcessListener -> onStart(
            files.toList(),
            bindingContext.await(),
            compilerResources.await()
        )
        else -> error("")
    }
}

private suspend fun FileProcessListener.onProcess(
    file: KtFile,
    bindingContext: Deferred<BindingContext>,
    compilerResources: Deferred<CompilerResources>
) {
    when (this) {
        is PlainFileProcessListener -> onProcess(file)
        is TypeSolvingFileProcessListener -> onProcess(
            file,
            bindingContext.await(),
            compilerResources.await()
        )
        else -> error("")
    }
}

private suspend fun FileProcessListener.onProcessComplete(
    file: KtFile,
    findings: List<Finding>,
    bindingContext: Deferred<BindingContext>,
    compilerResources: Deferred<CompilerResources>
) {
    when (this) {
        is PlainFileProcessListener -> onProcessComplete(file, mapOf("a" to findings))
        is TypeSolvingFileProcessListener -> onProcessComplete(
            file,
            mapOf("a" to findings),
            bindingContext.await(),
            compilerResources.await()
        )
        else -> error("")
    }
}

private suspend fun FileProcessListener.onFinish(
    files: Flow<KtFile>,
    findings: DetektResult,
    bindingContext: Deferred<BindingContext>,
    compilerResources: Deferred<CompilerResources>
) {
    when (this) {
        is PlainFileProcessListener -> onFinish(
            files.toList(),
            findings
        )
        is TypeSolvingFileProcessListener -> onFinish(
            files.toList(),
            findings,
            bindingContext.await(),
            compilerResources.await()
        )
        else -> error("")
    }
}
