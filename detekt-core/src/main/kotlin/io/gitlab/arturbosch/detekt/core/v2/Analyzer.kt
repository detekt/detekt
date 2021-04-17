package io.gitlab.arturbosch.detekt.core.v2

import io.github.detekt.psi.absolutePath
import io.gitlab.arturbosch.detekt.api.v2.Detektion
import io.gitlab.arturbosch.detekt.api.v2.FileProcessListener
import io.gitlab.arturbosch.detekt.api.v2.Rule
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.*
import org.jetbrains.kotlin.psi.KtFile

suspend fun analyze(
    rules: Flow<Pair<Rule, Filter>>,
    files: Flow<KtFile>,
    fileProcessListeners: Flow<FileProcessListener>,
): Detektion {
    return myAnalyze(
        rules.reusable(UNLIMITED),
        files.reusable(UNLIMITED),
        fileProcessListeners.reusable(UNLIMITED),
    )
}

@OptIn(FlowPreview::class)
private suspend fun myAnalyze(
    rules: Flow<Pair<Rule, Filter>>,
    files: Flow<KtFile>,
    fileProcessListeners: Flow<FileProcessListener>,
): Detektion {
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
        .flatMapMerge { (file, ruleFindings) ->
            fileProcessListeners
                .fold(ruleFindings) { findings, listener -> listener.onProcessComplete(file, findings) }
                .asFlow()
        }
        .toList() // this toList() breaks the parallelism

    val filesList = files.toList()

    return fileProcessListeners.fold(Detektion(findings = findings)) { detektion, listener ->
        listener.onFinish(filesList, detektion)
    }
}
