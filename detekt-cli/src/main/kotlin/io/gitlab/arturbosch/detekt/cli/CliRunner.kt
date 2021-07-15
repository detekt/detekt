package io.gitlab.arturbosch.detekt.cli

import io.github.detekt.tooling.api.AnalysisResult
import io.github.detekt.tooling.api.DetektCli
import io.github.detekt.tooling.api.UnexpectedError
import io.github.detekt.tooling.internal.DefaultAnalysisResult
import io.github.detekt.tooling.internal.EmptyContainer
import io.gitlab.arturbosch.detekt.cli.runners.AstPrinter
import io.gitlab.arturbosch.detekt.cli.runners.ConfigExporter
import io.gitlab.arturbosch.detekt.cli.runners.VersionPrinter
import io.gitlab.arturbosch.detekt.core.v2.providers.ConsoleReportersProviderImpl
import io.gitlab.arturbosch.detekt.core.v2.providers.FileProcessListenersProviderImpl
import io.gitlab.arturbosch.detekt.core.v2.providers.KtFilesProviderImpl
import io.gitlab.arturbosch.detekt.core.v2.providers.OutputReportersProviderImpl
import io.gitlab.arturbosch.detekt.core.v2.providers.ReportingModifiersProviderImpl
import io.gitlab.arturbosch.detekt.core.v2.providers.ResolvedContextProviderImpl
import io.gitlab.arturbosch.detekt.core.v2.providers.RulesProviderImpl
import io.gitlab.arturbosch.detekt.core.v2.run
import kotlinx.coroutines.runBlocking

class CliRunner : DetektCli {

    override fun run(args: Array<String>): AnalysisResult = run(args, System.out, System.err)

    override fun run(args: Array<String>, outputChannel: Appendable, errorChannel: Appendable): AnalysisResult {
        val arguments: CliArgs = runCatching { parseArguments(args) }
            .getOrElse { return DefaultAnalysisResult(null, UnexpectedError(it)) }

        val specialRunner = when {
            arguments.showVersion -> VersionPrinter(outputChannel)
            arguments.generateConfig -> ConfigExporter(arguments, outputChannel)
            arguments.printAst -> AstPrinter(arguments, outputChannel)
            else -> null
        }

        return if (specialRunner != null) {
            runCatching { specialRunner.execute() }
                .map { DefaultAnalysisResult(EmptyContainer) }
                .getOrElse { DefaultAnalysisResult(null, UnexpectedError(it)) }
        } else {
            runBlocking {
                run(
                    filesProvider = KtFilesProviderImpl(TODO()),
                    resolvedContextProvider = ResolvedContextProviderImpl(environment, classpath),
                    ruleProvider = RulesProviderImpl(this),
                    fileProcessListenersProvider = FileProcessListenersProviderImpl(this),
                    reportingModifiersProvider = ReportingModifiersProviderImpl(this),
                    consoleReportersProvider = ConsoleReportersProviderImpl(this),
                    outputReportersProvider = OutputReportersProviderImpl(this),
                ).toAnalysisResult()
            }
        }
    }
}

private fun Any.toAnalysisResult(): AnalysisResult {
    TODO()
}
