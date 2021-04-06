package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.psiUtil.nextLeaf
import org.jetbrains.kotlin.psi.unpackFunctionLiteral
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
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
class UnnecessaryFilter(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "UnnecessaryFilter",
        Severity.Style,
        "filter() with other collection operations may be simplified.",
        Debt.FIVE_MINS
    )

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        val calleeExpression = expression.calleeExpression
        if (calleeExpression?.text != "filter") return

        val resolvedCall = expression.getResolvedCall(bindingContext) ?: return
        if (resolvedCall.resultingDescriptor.fqNameOrNull() !in filterFqNames) return

        expression.checkNextLeaf(sizeFqName)
        expression.checkNextLeaf(collectionCountFqName)
        expression.checkNextLeaf(sequencesCountFqName)
        expression.checkNextLeaf(isEmptyFqName, "any")
        expression.checkNextLeaf(isNotEmptyFqName, "none")
    }

    @Suppress("ReturnCount")
    private fun KtCallExpression.checkNextLeaf(leafName: FqName, correctOperator: String? = null) {
        val shortName = leafName.shortName().toString()
        val nextLeaf = this.nextLeaf { it.text == shortName }?.parent as? KtElement ?: return
        val resolvedCall = nextLeaf.getResolvedCall(bindingContext) ?: return

        if (resolvedCall.resultingDescriptor.fqNameOrNull() != leafName) return

        report(
            CodeSmell(
                issue,
                Entity.from(this),
                "'${this.text}' can be replaced by '${correctOperator ?: shortName} ${this.lambda()?.text}'"
            )
        )
    }

    private fun KtCallExpression.lambda(): KtLambdaExpression? {
        val argument = lambdaArguments.singleOrNull() ?: valueArguments.singleOrNull()
        return argument?.getArgumentExpression()?.unpackFunctionLiteral()
    }

    companion object {
        private val sizeFqName = FqName("kotlin.collections.List.size")
        private val isEmptyFqName = FqName("kotlin.collections.List.isEmpty")
        private val isNotEmptyFqName = FqName("kotlin.collections.isNotEmpty")
        private val collectionCountFqName = FqName("kotlin.collections.count")
        private val sequencesCountFqName = FqName("kotlin.sequences.count")
        private val filterFqNames = listOf(FqName("kotlin.collections.filter"), FqName("kotlin.sequences.filter"))
    }
}
