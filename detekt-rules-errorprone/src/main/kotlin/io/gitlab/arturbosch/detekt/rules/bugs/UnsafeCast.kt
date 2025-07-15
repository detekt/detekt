package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.Alias
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
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression

/**
 * Reports casts that will never succeed.
 *
 * <noncompliant>
 * fun foo(s: String) {
 *     println(s as Int)
 * }
 *
 * fun bar(s: String) {
 *     println(s as? Int)
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(s: Any) {
 *     println(s as Int)
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.16.0")
@Alias("UNCHECKED_CAST")
class UnsafeCast(config: Config) :
    Rule(
        config,
        "Cast operator throws an exception if the cast is not possible."
    ),
    RequiresAnalysisApi {

    @OptIn(KaExperimentalApi::class)
    override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS) {
        val operationToken = (expression.operationReference as? KtOperationReferenceExpression)?.operationSignTokenType
        if (operationToken != KtTokens.AS_KEYWORD && operationToken != KtTokens.AS_SAFE) return

        val isCastNeverSucceeds = analyze(expression) {
            expression
                .diagnostics(KaDiagnosticCheckerFilter.ONLY_COMMON_CHECKERS)
                .any { it is KaFirDiagnostic.CastNeverSucceeds }
        }

        if (isCastNeverSucceeds) {
            report(
                Finding(
                    Entity.from(expression),
                    "${expression.left.text} cast to ${expression.right?.text.orEmpty()} cannot succeed."
                )
            )
        }

        super.visitBinaryWithTypeRHSExpression(expression)
    }
}
