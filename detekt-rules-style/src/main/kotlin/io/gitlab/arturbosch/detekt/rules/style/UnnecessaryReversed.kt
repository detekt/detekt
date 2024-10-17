package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresFullAnalysis
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.isCalling
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * If a sort operation followed by a reverse operation or vise versa should be avoided, and both statements
 * should be replaced by single equivalent sort operation.
 *
 * <noncompliant>
 * listOf(1,2)
 *  .sorted()
 *  .asReversed()
 * </noncompliant>
 *
 * <compliant>
 * listOf(1,2)
 *  .sortedDescending()
 * </compliant>
 */
@RequiresFullAnalysis
class UnnecessaryReversed(
    config: Config,
) : Rule(
    config,
    "Use single sort operation instead of sorting followed by a reverse operation or vise-versa, " +
        "eg. use `.sortedByDescending { .. }` instead of `.sortedBy { }.asReversed()`",
) {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        if (!expression.isCalling(sortFunctionNames + reverseFunctionNames, bindingContext)) return

        val callFq = expression.asFqNameOrNull() ?: return
        val parentCalls = expression.getPrevCallInChainOrNull()

        val oppositeCalls = if (callFq in sortFunctionNames) reverseFunctionNames else sortFunctionNames

        val parentCall = parentCalls.find { parentExpression ->
            parentExpression.isCalling(oppositeCalls, bindingContext)
        } ?: return

        val parentCallFq = parentCall.asFqNameOrNull() ?: return

        val sortCallUsed = if (parentCallFq in sortFunctionNames) parentCallFq else callFq

        val isSequentialCall = parentCallFq == parentCalls.lastOrNull()?.asFqNameOrNull()

        val suggestion = oppositePairs[sortCallUsed]?.let {
            if (isSequentialCall) {
                "Replace `${parentCallFq.shortName()}().${callFq.shortName()}()` by a single `${it.shortName()}()`"
            } else {
                "Replace `${callFq.shortName()}()` following `${parentCallFq.shortName()}()` by a " +
                    "single `${it.shortName()}() call`"
            }
        } ?: description

        report(
            CodeSmell(
                entity = Entity.from(expression),
                message = suggestion,
                references = listOf(Entity.from(expression), Entity.from(parentCall)),
            ),
        )
    }

    private fun KtCallExpression.asFqNameOrNull(): FqName? = getResolvedCall(bindingContext)
        ?.resultingDescriptor
        ?.fqNameOrNull()

    private fun KtExpression.getPrevCallInChainOrNull(): List<KtCallExpression> =
        parent.collectDescendantsOfType<KtCallExpression>()
            .dropLastWhile { it.psiOrParent == psiOrParent }

    companion object {
        private val reverseFunctionNames = listOf(
            FqName("kotlin.collections.reversed"),
            FqName("kotlin.collections.asReversed"),
        )

        private val sortFunctionNames = listOf(
            FqName("kotlin.collections.sortedBy"),
            FqName("kotlin.collections.sortedByDescending"),
            FqName("kotlin.collections.sorted"),
            FqName("kotlin.collections.sortedDescending"),
        )

        private val oppositePairs = mapOf(
            FqName("kotlin.collections.sortedBy") to FqName("kotlin.collections.sortedByDescending"),
            FqName("kotlin.collections.sorted") to FqName("kotlin.collections.sortedDescending"),
        ).let { map ->
            map + map.map { it.value to it.key }.toMap()
        }
    }
}
