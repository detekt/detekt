package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.components.KaDiagnosticCheckerFilter
import org.jetbrains.kotlin.analysis.api.fir.diagnostics.KaFirDiagnostic
import org.jetbrains.kotlin.psi.KtSafeQualifiedExpression

/**
 * Reports unnecessary safe call operators (`?.`) that can be removed by the user.
 *
 * <noncompliant>
 * val a: String = ""
 * val b = a?.length
 * </noncompliant>
 *
 * <compliant>
 * val a: String? = null
 * val b = a?.length
 * </compliant>
 */
@ActiveByDefault(since = "1.16.0")
class UnnecessarySafeCall(config: Config) :
    Rule(
        config,
        "Unnecessary safe call operator detected."
    ),
    RequiresAnalysisApi {

    @OptIn(KaExperimentalApi::class)
    override fun visitSafeQualifiedExpression(expression: KtSafeQualifiedExpression) {
        super.visitSafeQualifiedExpression(expression)

        val isUnnecessarySafeCall = analyze(expression) {
            expression
                .diagnostics(KaDiagnosticCheckerFilter.ONLY_COMMON_CHECKERS)
                .any { it is KaFirDiagnostic.UnnecessarySafeCall }
        }

        if (isUnnecessarySafeCall) {
            report(
                Finding(
                    Entity.from(expression),
                    "${expression.text} contains an unnecessary " +
                        "safe call operator"
                )
            )
        }
    }
}
