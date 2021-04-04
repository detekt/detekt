package io.gitlab.arturbosch.detekt.core.v2

import io.github.detekt.psi.absolutePath
import io.gitlab.arturbosch.detekt.api.v2.FileProcessListener
import io.gitlab.arturbosch.detekt.api.v2.Finding
import io.gitlab.arturbosch.detekt.api.v2.Rule
import kotlinx.coroutines.FlowPreview
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

@FlowPreview
fun analyze(
    rules: Flow<Pair<Rule, Filter>>,
    files: Flow<KtFile>,
    fileProcessListeners: Flow<FileProcessListener>,
): Flow<Finding> {
    return flow {
        coroutineScope {
            emitAll(
                myAnalyze(
                    rules.reusable(UNLIMITED),
                    files.reusable(UNLIMITED),
                    fileProcessListeners.reusable(UNLIMITED),
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
): Flow<Finding> {
    return flow {
        fileProcessListeners.collect { listener -> listener.onStart(files.toList()) } // this toList() breaks the parallelism

        val findings = files
            .onEach { file ->
                fileProcessListeners.collect { listener ->
                    listener.onProcess(file)
                }
            }
            .flatMapMerge { file ->
                flow {
                    val path = file.absolutePath()
                    val findings = rules
                        .filter { (_, filter) -> filter.filter(path) }
                        .flatMapMerge { (rule, filter) ->
                            rule.invoke(file)
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
                    listener.onProcessComplete(file, findings)
                }
            }
            .flatMapMerge { (_, findings) -> findings.asFlow() }
            .reusable(UNLIMITED)


        fileProcessListeners.collect { listener ->
            listener.onFinish(files.toList(), findings.toList())
        }

        emitAll(findings)
    }
}
