package io.gitlab.arturbosch.detekt.core

import org.jetbrains.kotlin.cli.common.messages.AnalyzerWithCompilerReport
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.PlainTextMessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
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
): BindingContext {
    if (classpath.isEmpty()) {
        return BindingContext.EMPTY
    }

    val analyzer = AnalyzerWithCompilerReport(
        PrintingMessageCollector(System.err, DetektMessageRenderer, true),
        environment.configuration.languageVersionSettings
    )
    analyzer.analyzeAndReport(files) {
        TopDownAnalyzerFacadeForJVM.analyzeFilesWithJavaIntegration(
            environment.project, files, NoScopeRecordCliBindingTrace(),
            environment.configuration, environment::createPackagePartProvider,
            ::FileBasedDeclarationProviderFactory
        )
    }
    return analyzer.analysisResult.bindingContext
}

private object DetektMessageRenderer : PlainTextMessageRenderer() {
    override fun getName() = "detekt message renderer"
    override fun getPath(location: CompilerMessageSourceLocation) = location.path
    override fun render(
        severity: CompilerMessageSeverity,
        message: String,
        location: CompilerMessageSourceLocation?,
    ): String {
        if (!severity.isError) return ""
        return super.render(severity, message, location)
    }
}
