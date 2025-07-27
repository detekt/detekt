package io.gitlab.arturbosch.detekt.rules.bugs

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import dev.detekt.psi.isNullable
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtTypeReference

/**
 * Reports cast of nullable variable to non-null type. Cast like this can hide `null`
 * problems in your code. The compliant code would be that which will correctly check
 *  for two things (nullability and type) and not just one (cast).
 *
 * <noncompliant>
 * fun foo(bar: Any?) {
 *     val x = bar as String
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo(bar: Any?) {
 *     val x = checkNotNull(bar) as String
 * }
 *
 * // Alternative
 * fun foo(bar: Any?) {
 *     val x = (bar ?: error("null assertion message")) as String
 * }
 * </compliant>
 */
class CastNullableToNonNullableType(config: Config) :
    Rule(
        config,
        "Nullable type to non-null type cast is found. Consider using two assertions, `null` assertions and type cast"
    ),
    RequiresAnalysisApi {

    @Configuration("Whether platform types should be considered as non-nullable and ignored by this rule")
    private val ignorePlatformTypes: Boolean by config(true)

    @Suppress("ReturnCount")
    override fun visitBinaryWithTypeRHSExpression(expression: KtBinaryExpressionWithTypeRHS) {
        super.visitBinaryWithTypeRHSExpression(expression)

        val operationReference = expression.operationReference
        if (operationReference.getReferencedNameElementType() != KtTokens.AS_KEYWORD) return
        if (expression.left.text == KtTokens.NULL_KEYWORD.value) return
        val typeRef = expression.right ?: return
        if (typeRef.isNullable()) return
        if (!expression.left.isNullable(!ignorePlatformTypes)) return

        val message =
            "Use separate `null` assertion and type cast like ('(${expression.left.text} ?: " +
                "error(\"null assertion message\")) as ${typeRef.text}') instead of '${expression.text}'."
        report(Finding(Entity.from(operationReference), message))
    }

    private fun KtTypeReference.isNullable(): Boolean {
        analyze(this) {
            return type.canBeNull
        }
    }
}
