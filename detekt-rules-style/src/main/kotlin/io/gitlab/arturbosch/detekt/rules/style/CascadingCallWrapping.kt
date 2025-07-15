package io.gitlab.arturbosch.detekt.rules.style

import io.github.detekt.psi.absolutePath
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.TextLocation
import io.gitlab.arturbosch.detekt.api.config
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtUnaryExpression
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset

/**
 * Requires that all chained calls are placed on a new line if a preceding one is.
 *
 * <noncompliant>
 * foo()
 *   .bar().baz()
 * </noncompliant>
 *
 * <compliant>
 * foo().bar().baz()
 *
 * foo()
 *   .bar()
 *   .baz()
 * </compliant>
 */
class CascadingCallWrapping(config: Config) : Rule(
    config,
    "If a chained call is wrapped to a new line, subsequent chained calls should be as well."
) {

    @Configuration("require trailing elvis expressions to be wrapped on a new line")
    private val includeElvis: Boolean by config(true)

    override fun visitQualifiedExpression(expression: KtQualifiedExpression) {
        super.visitQualifiedExpression(expression)

        checkExpression(expression, callExpression = expression.selectorExpression)
    }

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)

        if (includeElvis && expression.operationToken == KtTokens.ELVIS) {
            checkExpression(expression, callExpression = expression.right)
        }
    }

    private fun checkExpression(expression: KtExpression, callExpression: KtExpression?) {
        if (!expression.containsNewline() && expression.receiverContainsNewline()) {
            val callTextOrEmpty = callExpression?.text?.let { " `$it`" }.orEmpty()
            report(
                Finding(
                    entity = expression.toErrorReportEntity(),
                    message = "Chained call$callTextOrEmpty should be wrapped to a new line since preceding calls were."
                )
            )
        }
    }

    @Suppress("ReturnCount")
    private fun KtExpression.containsNewline(): Boolean {
        val lhs: KtExpression
        val rhs: KtExpression

        when (this) {
            is KtQualifiedExpression -> {
                lhs = receiverExpression
                rhs = selectorExpression ?: return false
            }
            is KtBinaryExpression -> {
                if (operationToken != KtTokens.ELVIS) return false
                lhs = left ?: return false
                rhs = right ?: return false
            }
            else -> return false
        }

        val receiverEnd = lhs.startOffsetInParent + lhs.textLength
        val selectorStart = rhs.startOffsetInParent

        return (receiverEnd..<selectorStart).any { text[it] == '\n' }
    }

    private fun KtExpression.receiverContainsNewline(): Boolean {
        val lhs = when (this) {
            is KtQualifiedExpression -> receiverExpression
            is KtBinaryExpression -> left ?: return false
            else -> return false
        }

        return when (lhs) {
            is KtQualifiedExpression -> lhs.containsNewline()
            is KtUnaryExpression -> (lhs.baseExpression as? KtQualifiedExpression)?.containsNewline() == true
            else -> false
        }
    }

    private fun KtExpression.toErrorReportEntity(): Entity {
        return when (this) {
            is KtQualifiedExpression -> Entity.from(this.selectorExpression ?: this)
            is KtBinaryExpression -> {
                val rhs = this.right ?: return Entity.from(this)
                val operationSourceLocation = Location.from(operationReference).source
                val rhsSourceLocation = Location.from(rhs).endSource
                val textLocation = TextLocation(operationReference.startOffset, rhs.endOffset)
                Entity.from(
                    this,
                    Location(operationSourceLocation, rhsSourceLocation, textLocation, containingFile.absolutePath())
                )
            }
            else -> Entity.from(this)
        }
    }
}
