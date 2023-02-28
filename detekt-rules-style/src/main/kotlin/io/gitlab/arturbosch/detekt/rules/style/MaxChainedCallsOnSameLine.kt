package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtUnaryExpression
import org.jetbrains.kotlin.resolve.BindingContext

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
@RequiresTypeResolution
class MaxChainedCallsOnSameLine(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        id = javaClass.simpleName,
        severity = Severity.Style,
        description = "Chained calls beyond the maximum should be wrapped to a new line.",
        debt = Debt.FIVE_MINS,
    )

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
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(expression),
                    message = "$chainedCalls chained calls on a single line; more than $maxChainedCalls calls should " +
                        "be wrapped to a new line."
                )
            )
        }
    }

    private fun KtExpression.countChainedCalls(): Int {
        return when (this) {
            is KtQualifiedExpression -> when {
                receiverExpression.isReferenceToPackage() || callOnNewLine() -> 0
                else -> receiverExpression.countChainedCalls() + 1
            }
            is KtUnaryExpression -> baseExpression?.countChainedCalls() ?: 0
            else -> 0
        }
    }

    private fun KtExpression.isReferenceToPackage(): Boolean {
        val selectorOrThis = (this as? KtQualifiedExpression)?.selectorExpression ?: this
        if (selectorOrThis !is KtReferenceExpression) return false
        return bindingContext[BindingContext.REFERENCE_TARGET, selectorOrThis] is PackageViewDescriptor
    }

    private fun KtQualifiedExpression.callOnNewLine(): Boolean {
        val receiver = receiverExpression
        val selector = selectorExpression ?: return false

        val receiverEnd = receiver.startOffsetInParent + receiver.textLength
        val selectorStart = selector.startOffsetInParent

        return text
            .subSequence(startIndex = receiverEnd, endIndex = selectorStart)
            .contains('\n')
    }
}
