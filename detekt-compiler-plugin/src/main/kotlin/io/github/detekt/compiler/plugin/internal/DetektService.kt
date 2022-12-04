package io.github.detekt.compiler.plugin.internal

import io.github.detekt.tooling.api.DetektProvider
import io.github.detekt.tooling.api.InvalidConfig
import io.github.detekt.tooling.api.MaxIssuesReached
import io.github.detekt.tooling.api.UnexpectedError
import io.github.detekt.tooling.api.spec.ProcessingSpec
import io.gitlab.arturbosch.detekt.api.UnstableApi
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

internal class DetektService(
    private val log: MessageCollector,
    private val spec: ProcessingSpec
) {

    @OptIn(UnstableApi::class)
    @Suppress("ForbiddenComment")
    fun analyze(files: Collection<KtFile>, context: BindingContext) {
        val detekt = DetektProvider.load().get(spec)
        val result = detekt.run(files, context)
        log.info("${files.size} files analyzed")
        result.container?.let { log.reportFindings(it) }
        log.info("Success?: ${result.error == null}")
        when (val error = result.error) {
            is UnexpectedError -> throw error
            is MaxIssuesReached -> log.warn(error.localizedMessage) // TODO: handle MaxIssuePolicy
            is InvalidConfig -> log.warn(error.localizedMessage)
            null -> { } // nothing to do in this case
        }
    }
}
