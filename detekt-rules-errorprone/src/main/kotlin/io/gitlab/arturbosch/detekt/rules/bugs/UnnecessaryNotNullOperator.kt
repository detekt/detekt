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
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtUnaryExpression

/**
 * Reports unnecessary not-null operator usage (!!) that can be removed by the user.
 *
 * <noncompliant>
 * val a = 1
 * val b = a!!
 * </noncompliant>
 *
 * <compliant>
 * val a = 1
 * val b = a
 * </compliant>
 */
@ActiveByDefault(since = "1.16.0")
class UnnecessaryNotNullOperator(config: Config) :
    Rule(
        config,
        "Unnecessary not-null unary operator (!!) detected."
    ),
    RequiresAnalysisApi {

    @OptIn(KaExperimentalApi::class)
    override fun visitUnaryExpression(expression: KtUnaryExpression) {
        super.visitUnaryExpression(expression)

        if (expression.operationToken != KtTokens.EXCLEXCL) return

        val compilerReports = analyze(expression) {
            expression.diagnostics(KaDiagnosticCheckerFilter.ONLY_COMMON_CHECKERS)
        }
        if (compilerReports.any { it is KaFirDiagnostic.UnnecessaryNotNullAssertion }) {
            report(
                Finding(
                    Entity.from(expression),
                    "${expression.text} contains an unnecessary " +
                        "not-null (!!) operators"
                )
            )
        }
    }
}
