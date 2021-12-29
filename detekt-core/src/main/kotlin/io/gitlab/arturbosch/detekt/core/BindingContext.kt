package io.gitlab.arturbosch.detekt.core

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
import org.jetbrains.kotlin.resolve.lazy.declarations.FileBasedDeclarationProviderFactory

internal fun generateBindingContext(
    environment: KotlinCoreEnvironment,
    classpath: List<String>,
    files: List<KtFile>,
    debugPrinter: (() -> String) -> Unit,
    warningPrinter: (String) -> Unit,
): BindingContext {
    if (classpath.isEmpty()) {
        return BindingContext.EMPTY
    }

    val messageCollector = DetektMessageCollector(
        debugPrinter = debugPrinter,
        minSeverity = CompilerMessageSeverity.ERROR,
    )

    val analyzer = AnalyzerWithCompilerReport(
        messageCollector,
        environment.configuration.languageVersionSettings,
    )
    analyzer.analyzeAndReport(files) {
        TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
            environment.project,
            files,
            NoScopeRecordCliBindingTrace(),
            environment.configuration,
            environment::createPackagePartProvider,
            ::FileBasedDeclarationProviderFactory
        )
    }

    if (messageCollector.messages > 0) {
        warningPrinter(
            "The BindingContext was created with ${messageCollector.messages} issues. " +
                "Run detekt with --debug to see the error messages."
        )
    }

    return analyzer.analysisResult.bindingContext
}

private class DetektMessageCollector(
    private val debugPrinter: (() -> String) -> Unit,
    private val minSeverity: CompilerMessageSeverity,
) : MessageCollector by MessageCollector.NONE {
    var messages = 0
        private set

    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageSourceLocation?) {
        if (severity.ordinal <= minSeverity.ordinal) {
            debugPrinter { DetektMessageRenderer.render(severity, message, location) }
            messages++
        }
    }
}

private object DetektMessageRenderer : PlainTextMessageRenderer() {
    override fun getName() = "detekt message renderer"
    override fun getPath(location: CompilerMessageSourceLocation) = location.path
}
