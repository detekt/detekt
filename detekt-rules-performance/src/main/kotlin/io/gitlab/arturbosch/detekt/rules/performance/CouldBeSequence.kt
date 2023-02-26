package io.gitlab.arturbosch.detekt.rules.performance

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
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForReceiver
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelectorOrThis
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * Long chains of collection operations will have a performance penalty due to a new list being created for each call. Consider using sequences instead. Read more about this in the [documentation](https://kotlinlang.org/docs/sequences.html)
 *
 * <noncompliant>
 * listOf(1, 2, 3, 4).map { it*2 }.filter { it < 4 }.map { it*it }
 * </noncompliant>
 *
 * <compliant>
 * listOf(1, 2, 3, 4).asSequence().map { it*2 }.filter { it < 4 }.map { it*it }.toList()
 *
 * listOf(1, 2, 3, 4).map { it*2 }
 * </compliant>
 */
@RequiresTypeResolution
class CouldBeSequence(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "CouldBeSequence",
        Severity.Performance,
        "Several chained collection operations that should be a sequence.",
        Debt.FIVE_MINS
    )

    @Configuration("the number of chained collection operations required to trigger rule")
    private val threshold: Int by config(defaultValue = 3)

    private var visitedCallExpressions = mutableListOf<KtExpression>()

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (visitedCallExpressions.contains(expression)) return

        if (!expression.isCalling(operationsFqNames)) return

        var counter = 1
        var nextCall = expression.nextChainedCall()
        while (counter < threshold && nextCall != null) {
            visitedCallExpressions += nextCall
            if (!nextCall.isCalling(operationsFqNames)) {
                break
            }

            counter++
            nextCall = nextCall.nextChainedCall()
        }

        if (counter >= threshold) {
            val message = "${expression.text} could be .asSequence().${expression.text}"
            report(CodeSmell(issue, Entity.from(expression), message))
        }
    }

    private fun KtExpression.nextChainedCall(): KtExpression? {
        val expression = this.getQualifiedExpressionForSelectorOrThis()
        return expression.getQualifiedExpressionForReceiver()?.selectorExpression
    }

    private fun KtExpression.isCalling(fqNames: List<FqName>): Boolean {
        val calleeText = (this as? KtCallExpression)?.calleeExpression?.text ?: this.text
        val targetFqNames = fqNames.filter { it.shortName().asString() == calleeText }
        if (targetFqNames.isEmpty()) return false
        return getResolvedCall(bindingContext)?.resultingDescriptor?.fqNameOrNull() in targetFqNames
    }

    companion object {
        private val operationsFqNames = listOf(
            FqName("kotlin.collections.filter"),
            FqName("kotlin.collections.filterIndexed"),
            FqName("kotlin.collections.map"),
            FqName("kotlin.collections.mapIndexed"),
            FqName("kotlin.collections.flatMap"),
            FqName("kotlin.collections.flatMapIndexed"),
            FqName("kotlin.collections.reduce"),
            FqName("kotlin.collections.zip")
        )
    }
}
