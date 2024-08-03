package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.isCalling
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull

/**
 * `if` expressions that either check for not-null and return `null` in the false case or check for `null` and returns
 * `null` in the truthy case are better represented as `?.let {}` blocks.
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
@RequiresTypeResolution
class UnnecessaryReversed(
    config: Config,
) : Rule(
    config,
    "Use single sort operation instead of sorting followed by `.asReversed()`, " +
        "eg. use `.sortedByDescending { .. }` instead of `.sortedBy { it.priority }.asReversed()`",
) {
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (!expression.isCalling(reverseFunctionNames, bindingContext)) return

        val parentCall = expression.getPrevCallInChainOrNull() ?: return

        val parentCallFqName = parentCall.getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.fqNameOrNull()
            ?: return

        val callFqName = expression.getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.fqNameOrNull()
            ?: return

        if (!parentCall.isCalling(sortFunctionNames, bindingContext)) return

        val suggestion = reversePairs[parentCallFqName]?.let {
            "Replace `${parentCallFqName.shortName()}().${callFqName.shortName()}()` by single `${it.shortName()}()`"
        } ?: description

        report(
            CodeSmell(
                entity = Entity.from(expression),
                message = suggestion,
                references = listOf(Entity.from(expression), Entity.from(parentCall)),
            ),
        )
    }

    private fun KtExpression.getPrevCallInChainOrNull(): KtCallExpression? =
        parent.collectDescendantsOfType<KtCallExpression>()
            .dropLastWhile { it.psiOrParent == psiOrParent }
            .lastOrNull()

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

        private val reversePairs = mapOf(
            FqName("kotlin.collections.sortedBy") to FqName("kotlin.collections.sortedByDescending"),
            FqName("kotlin.collections.sorted") to FqName("kotlin.collections.sortedDescending"),
        ).let { map ->
            map + map.map { it.value to it.key }.toMap()
        }
    }
}
