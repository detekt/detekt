package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiver
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelectorOrThis
import org.jetbrains.kotlin.psi.psiUtil.siblings
import org.jetbrains.kotlin.psi.unpackFunctionLiteral
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * Unnecessary filters add complexity to the code and accomplish nothing. They should be removed.
 *
 * <noncompliant>
 * val x = listOf(1, 2, 3)
 *      .filter { it > 1 }
 *      .count()
 *
 * val x = listOf(1, 2, 3)
 *      .filter { it > 1 }
 *      .isEmpty()
 * </noncompliant>
 *
 * <compliant>
 * val x = listOf(1, 2, 3)
 *      .count { it > 2 }
 *
 * val x = listOf(1, 2, 3)
 *      .none { it > 1 }
 * </compliant>
 *
 */
@RequiresTypeResolution
@ActiveByDefault(since = "1.21.0")
class UnnecessaryFilter(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "UnnecessaryFilter",
        "`filter()` with other collection operations may be simplified.",
        Debt.FIVE_MINS
    )

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.isCalling(filterFqNames)) return
        val lambdaArgumentText = expression.lambda()?.text ?: return

        val qualifiedOrCall = expression.getQualifiedExpressionForSelectorOrThis()
        val nextCall = qualifiedOrCall.nextCall() ?: return

        secondCalls.firstOrNull { nextCall.isCalling(it.fqName) }?.let {
            val message = "'${expression.text}' can be replaced by '${it.correctOperator} $lambdaArgumentText'"
            report(CodeSmell(issue, Entity.from(expression), message))
        }
    }

    private fun KtCallExpression.lambda(): KtLambdaExpression? {
        val argument = lambdaArguments.singleOrNull() ?: valueArguments.singleOrNull()
        return argument?.getArgumentExpression()?.unpackFunctionLiteral()
    }

    private fun KtExpression.isCalling(fqNames: List<FqName>): Boolean {
        val calleeText = (this as? KtCallExpression)?.calleeExpression?.text ?: this.text
        val targetFqNames = fqNames.filter { it.shortName().asString() == calleeText }
        if (targetFqNames.isEmpty()) return false
        return getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameOrNull() in targetFqNames
    }

    private fun KtExpression.isCalling(fqName: FqName) = isCalling(listOf(fqName))

    @Suppress("ReturnCount")
    private fun KtExpression.nextCall(): KtExpression? {
        getQualifiedExpressionForReceiver()?.selectorExpression?.let { return it }

        val property = parentProperty()
        val propertyDescriptor = property?.descriptor()
        if (propertyDescriptor != null) {
            val singleReferrer = getReferrers(property, propertyDescriptor).singleOrNull()
            val qualified = singleReferrer?.getQualifiedExpressionForReceiver()
            if (qualified?.parentProperty() != null || qualified?.parentReturn() != null) {
                val receiver = qualified.receiverExpression as? KtNameReferenceExpression
                if (receiver != null && receiver.descriptor() == propertyDescriptor) {
                    return qualified.selectorExpression
                }
            }
        }

        return null
    }

    private fun KtExpression.parentProperty(): KtProperty? =
        (parent as? KtProperty)?.takeIf { it.initializer == this }

    private fun KtExpression.parentReturn(): KtReturnExpression? =
        (parent as? KtReturnExpression)?.takeIf { it.returnedExpression == this }

    private fun getReferrers(
        property: KtProperty,
        propertyDescriptor: DeclarationDescriptor
    ): Sequence<KtNameReferenceExpression> {
        return property.siblings(forward = true, withItself = false).flatMap { sibling ->
            sibling.collectDescendantsOfType<KtNameReferenceExpression> { it.descriptor() == propertyDescriptor }
        }
    }

    private fun KtProperty.descriptor(): DeclarationDescriptor? =
        bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, this]

    private fun KtReferenceExpression.descriptor(): DeclarationDescriptor? =
        bindingContext[BindingContext.REFERENCE_TARGET, this]

    private data class SecondCall(val fqName: FqName, val correctOperator: String = fqName.shortName().asString())

    companion object {
        private val filterFqNames = listOf(
            FqName("kotlin.collections.filter"),
            FqName("kotlin.sequences.filter"),
        )

        private val secondCalls = listOf(
            SecondCall(FqName("kotlin.collections.List.size"), "count"),
            SecondCall(FqName("kotlin.collections.List.isEmpty"), "none"),
            SecondCall(FqName("kotlin.collections.isNotEmpty"), "any"),
            SecondCall(FqName("kotlin.collections.count")),
            SecondCall(FqName("kotlin.sequences.count")),
        )
    }
}
