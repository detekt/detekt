package io.github.detekt.parser

import org.jetbrains.kotlin.cli.common.messages.AnalyzerWithCompilerReport
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.PlainTextMessageRenderer
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.NoScopeRecordCliBindingTrace
import org.jetbrains.kotlin.cli.jvm.compiler.TopDownAnalyzerFacadeForJVM
import org.jetbrains.kotlin.config.languageVersionSettings
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext

fun generateBindingContext(
    environment: KotlinCoreEnvironment,
    files: List<KtFile>,
    debugPrinter: (() -> String) -> Unit,
    warningPrinter: (String) -> Unit,
): BindingContext {
    val messageCollector = DetektMessageCollector(
        minSeverity = CompilerMessageSeverity.ERROR,
        debugPrinter = debugPrinter,
        warningPrinter = warningPrinter,
    )

    val analyzer = AnalyzerWithCompilerReport(
        messageCollector,
        environment.configuration.languageVersionSettings,
        false,
    )
    analyzer.analyzeAndReport(files) {
        TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
            environment.project,
            files,
            NoScopeRecordCliBindingTrace(environment.project),
            environment.configuration,
            environment::createPackagePartProvider
        )
    }

    messageCollector.printIssuesCountIfAny()

    return analyzer.analysisResult.bindingContext
}

internal class DetektMessageCollector(
    private val minSeverity: CompilerMessageSeverity,
    private val debugPrinter: (() -> String) -> Unit,
    private val warningPrinter: (String) -> Unit,
) : MessageCollector by MessageCollector.NONE {
    private var messages = 0

    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageSourceLocation?) {
        if (severity.ordinal <= minSeverity.ordinal) {
            debugPrinter { DetektMessageRenderer.render(severity, message, location) }
            messages++
        }
    }

    fun printIssuesCountIfAny() {
        if (messages > 0) {
            warningPrinter(
                "There were $messages compiler errors found during analysis. This affects accuracy of reporting.\n" +
                    "Run detekt CLI with --debug or set `detekt { debug = true }` in Gradle to see the error messages."
            )
        }
    }
}

private object DetektMessageRenderer : PlainTextMessageRenderer() {
    override fun getName() = "detekt message renderer"
    override fun getPath(location: CompilerMessageSourceLocation) = location.path
}
