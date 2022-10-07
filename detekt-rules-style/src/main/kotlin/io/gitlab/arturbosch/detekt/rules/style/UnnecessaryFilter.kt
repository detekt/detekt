package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiver
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelectorOrThis
import org.jetbrains.kotlin.psi.unpackFunctionLiteral
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
 * }
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
        Severity.Style,
        "`filter()` with other collection operations may be simplified.",
        Debt.FIVE_MINS
    )

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.isCalling(filterFqNames)) return
        val lambdaArgumentText = expression.lambda()?.text ?: return

        val qualifiedOrCall = expression.getQualifiedExpressionForSelectorOrThis()
        val nextCall = qualifiedOrCall.getQualifiedExpressionForReceiver()?.selectorExpression ?: return

        secondCalls.forEach {
            if (nextCall.isCalling(it.fqName)) {
                val message = "'${expression.text}' can be replaced by '${it.correctOperator} $lambdaArgumentText'"
                report(CodeSmell(issue, Entity.from(expression), message))
            }
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

    private data class SecondCall(val fqName: FqName, val correctOperator: String = fqName.shortName().asString())

    companion object {
        private val filterFqNames = listOf(
            FqName("kotlin.collections.filter"),
            FqName("kotlin.sequences.filter")
        )

        private val secondCalls = listOf(
            SecondCall(FqName("kotlin.collections.List.size"), "count"),
            SecondCall(FqName("kotlin.collections.List.isEmpty"), "any"),
            SecondCall(FqName("kotlin.collections.isNotEmpty"), "none"),
            SecondCall(FqName("kotlin.collections.count")),
            SecondCall(FqName("kotlin.sequences.count"))
        )
    }
}
