package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RequiresAnalysisApi
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.analysis.api.fir.diagnostics.KaFirDiagnostic
import org.jetbrains.kotlin.psi.KtExpression

/**
 * Reports unreachable code.
 * Code can be unreachable because it is behind return, throw, continue or break expressions.
 * This unreachable code should be removed as it serves no purpose.
 *
 * <noncompliant>
 * for (i in 1..2) {
 *     break
 *     println() // unreachable
 * }
 *
 * throw IllegalArgumentException()
 * println() // unreachable
 *
 * fun f() {
 *     return
 *     println() // unreachable
 * }
 * </noncompliant>
 */
@ActiveByDefault(since = "1.0.0")
class UnreachableCode(config: Config) :
    Rule(
        config,
        "Unreachable code detected. This code should be removed."
    ),
    RequiresAnalysisApi {

    @OptIn(KaExperimentalApi::class)
    override fun visitExpression(expression: KtExpression) {
        super.visitExpression(expression)

        val isUnreachableCode = analyze(expression) {
            expression
                .diagnostics(KaDiagnosticCheckerFilter.EXTENDED_AND_COMMON_CHECKERS)
                .any { it is KaFirDiagnostic.UnreachableCode || it is KaFirDiagnostic.UselessElvis }
        }

        if (isUnreachableCode) {
            report(
                Finding(
                    Entity.from(expression),
                    "This expression is unreachable code which should either be used or removed."
                )
            )
        }
    }
}
