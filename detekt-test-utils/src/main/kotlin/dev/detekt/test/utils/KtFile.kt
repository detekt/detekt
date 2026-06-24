package dev.detekt.test.utils

import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaCompilationResult
import org.jetbrains.kotlin.analysis.api.components.KaCompilerTarget
import org.jetbrains.kotlin.analysis.api.diagnostics.KaDiagnosticWithPsi
import org.jetbrains.kotlin.analysis.api.diagnostics.KaSeverity
import org.jetbrains.kotlin.cli.create
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.diagnostics.PsiDiagnosticUtils
import org.jetbrains.kotlin.psi.KtFile

/**
 * Throw an exception if the KtFile had any compilation error
 */
@OptIn(KaExperimentalApi::class)
internal fun KtFile.checkNoCompilationErrors() {
    val file = this
    analyze(file) {
        val result = compile(
            file,
            CompilerConfiguration.create(),
            KaCompilerTarget.Jvm(isTestMode = false, compiledClassHandler = null, debuggerExtension = null)
        ) {
            it.severity != KaSeverity.ERROR
        }

        if (result is KaCompilationResult.Failure) {
            val errors = result.errors.joinToString("\n") {
                if (it is KaDiagnosticWithPsi<*>) {
                    val lineAndColumn = PsiDiagnosticUtils.offsetToLineAndColumn(
                        it.psi.containingFile.viewProvider.document,
                        it.psi.textOffset
                    )
                    "${it.severity.name} ${it.defaultMessage} (${it.psi.containingFile.name}:${lineAndColumn.line}:${lineAndColumn.column})"
                } else {
                    "${it.severity.name} ${it.defaultMessage}"
                }
            }

            error(errors)
        }
    }
}
