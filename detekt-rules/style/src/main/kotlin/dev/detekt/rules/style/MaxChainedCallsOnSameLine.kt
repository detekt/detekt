package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaPackageSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtUnaryExpression
import org.jetbrains.kotlin.psi.psiUtil.findDescendantOfType
import org.jetbrains.kotlin.psi.psiUtil.getStartOffsetIn

/**
 * Limits the number of chained calls which can be placed on a single line.
 *
 * <noncompliant>
 * a().b().c().d().e().f()
 * </noncompliant>
 *
 * <compliant>
 * a().b().c()
 *   .d().e().f()
 * </compliant>
 */
class MaxChainedCallsOnSameLine(config: Config) :
    Rule(
        config,
        "Chained calls beyond the maximum should be wrapped to a new line."
    ),
    RequiresAnalysisApi {

    @Configuration("maximum chained calls allowed on a single line")
    private val maxChainedCalls: Int by config(defaultValue = 5)

    override fun visitQualifiedExpression(expression: KtQualifiedExpression) {
        super.visitQualifiedExpression(expression)

        val parent = expression.parent

        // skip if the parent is also a call on the same line to avoid duplicated warnings
        if (parent is KtQualifiedExpression && !parent.callOnNewLine()) return

        if (parent is KtImportDirective || parent is KtPackageDirective) return

        val chainedCalls = expression.countChainedCalls() + 1
        if (chainedCalls > maxChainedCalls) {
            report(
                Finding(
                    entity = Entity.from(expression),
                    message = "$chainedCalls chained calls on a single line; more than $maxChainedCalls calls should " +
                        "be wrapped to a new line."
                )
            )
        }
    }

    private fun KtExpression.countChainedCalls(): Int =
        when (this) {
            is KtQualifiedExpression -> when {
                receiverExpression.isReferenceToPackageOrClass() || callOnNewLine() -> 0
                else -> receiverExpression.countChainedCalls() + 1
            }

            is KtUnaryExpression -> baseExpression?.countChainedCalls() ?: 0

            else -> 0
        }

    private fun KtExpression.isReferenceToPackageOrClass(): Boolean {
        val selectorOrThis = (this as? KtQualifiedExpression)?.selectorExpression ?: this
        if (selectorOrThis !is KtReferenceExpression) return false
        val symbol = analyze(selectorOrThis) { selectorOrThis.mainReference.resolveToSymbol() }
        return symbol is KaPackageSymbol || symbol is KaClassSymbol
    }

    private fun KtQualifiedExpression.callOnNewLine(): Boolean {
        val receiver = receiverExpression
        val selector = selectorExpression ?: return false

        val receiverEnd = getReceiverEndPosition(receiver)
        val selectorStart = selector.startOffsetInParent

        return text
            .subSequence(startIndex = receiverEnd, endIndex = selectorStart + 1)
            .contains('\n')
    }

    private fun getReceiverEndPosition(receiver: KtExpression): Int {
        fun indexOfReceiverEnd() = receiver.startOffsetInParent + receiver.textLength - 1
        var receiverDotQualifiedExpressionPassed = false
        val callExpression = receiver.findDescendantOfType<KtCallExpression>(
            canGoInside = {
                val canGoInside =
                    it == receiver || (receiverDotQualifiedExpressionPassed.not() || it !is KtDotQualifiedExpression)
                if (it is KtDotQualifiedExpression) {
                    receiverDotQualifiedExpressionPassed = true
                }
                canGoInside
            }
        )
        return callExpression?.calleeExpression?.let {
            it.getStartOffsetIn(receiver.parent) + it.textLength - 1
        } ?: indexOfReceiverEnd()
    }
}
