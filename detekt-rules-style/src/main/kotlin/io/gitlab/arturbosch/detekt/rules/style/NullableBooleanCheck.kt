package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression

/**
 * Detects nullable boolean checks which use an elvis expression `?:` rather than equals `==`.
 *
 * Per the [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html#nullable-boolean-values-in-conditions)
 * converting a nullable boolean property to non-null should be done via `!= false` or `== true`
 * rather than `?: true` or `?: false` (respectively).
 *
 * <noncompliant>
 * value ?: true
 * value ?: false
 * </noncompliant>
 *
 * <compliant>
 * value != false
 * value == true
 * </compliant>
 */
class NullableBooleanCheck(config: Config) :
    Rule(
        config,
        "Nullable boolean check should use `==` rather than `?:`"
    ),
    RequiresAnalysisApi {

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        if (expression.operationToken == KtTokens.ELVIS &&
            expression.right?.isBooleanConstant() == true &&
            expression.left?.isNullableBoolean() == true
        ) {
            val messageSuffix =
                if (expression.right?.text == "true") {
                    "`!= false` rather than `?: true`"
                } else {
                    "`== true` rather than `?: false`"
                }
            report(
                Finding(
                    entity = Entity.from(expression),
                    message = "The nullable boolean check `${expression.text}` should use $messageSuffix",
                )
            )
        }

        super.visitBinaryExpression(expression)
    }

    private fun KtExpression.isBooleanConstant() = node.elementType == KtNodeTypes.BOOLEAN_CONSTANT

    private fun KtExpression.isNullableBoolean() = analyze(this) {
        val type = expressionType
        type?.isBooleanType == true && type.nullability.isNullable
    }
}
