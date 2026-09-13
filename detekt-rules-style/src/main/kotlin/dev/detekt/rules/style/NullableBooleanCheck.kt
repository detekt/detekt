package dev.detekt.rules.style

import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtWhenCondition
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.KtWhileExpressionBase
import org.jetbrains.kotlin.psi.psiUtil.parents

/**
 * Detects nullable boolean checks which use an elvis expression `?:` rather than equals `==`.
 *
 * Per the [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html#nullable-boolean-values-in-conditions)
 * converting a nullable boolean property to non-null in **conditions** should be done via `!= false` or `== true`
 * rather than `?: true` or `?: false` (respectively).
 *
 * By default this rule only reports uses in conditionals (`if`/`while`/`do-while`/`when`).
 * Set [onlyConditionals] to `false` to also report assignments, returns, and other non-conditional uses.
 *
 * <noncompliant>
 * if (value ?: true) { }
 * if (value ?: false) { }
 * </noncompliant>
 *
 * <compliant>
 * if (value != false) { }
 * if (value == true) { }
 * val flag = value ?: true
 * </compliant>
 */
class NullableBooleanCheck(config: Config) :
    Rule(
        config,
        "Nullable boolean check should use `==` rather than `?:`"
    ),
    RequiresAnalysisApi {

    @Configuration("Whether to report only elvis expressions used in conditionals")
    private val onlyConditionals: Boolean by config(true)

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        if (expression.operationToken == KtTokens.ELVIS &&
            expression.right?.isBooleanConstant() == true &&
            expression.left?.isNullableBoolean() == true &&
            (!onlyConditionals || expression.isUsedInCondition())
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

    private fun KtExpression.isNullableBoolean() =
        analyze(this) {
            val type = expressionType
            type?.isBooleanType == true && type.isMarkedNullable
        }

    private fun KtExpression.isUsedInCondition(): Boolean {
        for (parent in parents) {
            when (parent) {
                is KtIfExpression -> return isInside(parent.condition)
                is KtWhileExpressionBase -> return isInside(parent.condition)
                is KtWhenExpression -> return isInside(parent.subjectExpression)
                is KtWhenCondition -> return true
                is KtBlockExpression, is KtFunction, is KtProperty -> return false
            }
        }
        return false
    }

    private fun PsiElement.isInside(ancestor: PsiElement?): Boolean =
        ancestor != null && PsiTreeUtil.isAncestor(ancestor, this, false)
}
