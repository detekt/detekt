package io.gitlab.arturbosch.detekt.cli

import dev.detekt.tooling.api.AnalysisResult
import dev.detekt.tooling.api.DetektCli
import dev.detekt.tooling.api.UnexpectedError
import dev.detekt.tooling.internal.DefaultAnalysisResult
import dev.detekt.tooling.internal.EmptyContainer
import io.gitlab.arturbosch.detekt.cli.runners.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.runners.Runner
import io.gitlab.arturbosch.detekt.cli.runners.VersionPrinter

class CliRunner : DetektCli {

    override fun run(args: Array<String>): AnalysisResult = run(args, System.out, System.err)

    override fun run(args: Array<String>, outputChannel: Appendable, errorChannel: Appendable): AnalysisResult {
        val arguments = runCatching { parseArguments(args) }
            .getOrElse { return DefaultAnalysisResult(null, UnexpectedError(it)) }

        val specialRunner = when {
            arguments.showVersion -> VersionPrinter(outputChannel)
            arguments.generateConfig != null -> ConfigExporter(arguments, outputChannel)
            else -> null
        }

        return if (specialRunner != null) {
            runCatching { specialRunner.execute() }
                .map { DefaultAnalysisResult(EmptyContainer) }
                .getOrElse { DefaultAnalysisResult(null, UnexpectedError(it)) }
        } else {
            Runner(arguments, outputChannel, errorChannel).call()
        }
    }
}
