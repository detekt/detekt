package io.github.detekt.compiler.plugin.internal

import dev.detekt.tooling.api.DetektProvider
import dev.detekt.tooling.api.InvalidConfig
import dev.detekt.tooling.api.IssuesFound
import dev.detekt.tooling.api.UnexpectedError
import dev.detekt.tooling.api.spec.ProcessingSpec
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtFile

internal class DetektService(
    private val log: MessageCollector,
    private val spec: ProcessingSpec,
) {

    fun analyze(files: Collection<KtFile>) {
        val detekt = DetektProvider.load().get(spec)
        val result = detekt.run(files)
        log.info("${files.size} files analyzed")
        result.container?.let { log.reportIssues(it) }
        log.info("Success?: ${result.error == null}")
        when (val error = result.error) {
            is UnexpectedError -> throw error
            is IssuesFound -> log.warn(error.localizedMessage)
            is InvalidConfig -> log.warn(error.localizedMessage)
            null -> { } // nothing to do in this case
        }
    }
}
