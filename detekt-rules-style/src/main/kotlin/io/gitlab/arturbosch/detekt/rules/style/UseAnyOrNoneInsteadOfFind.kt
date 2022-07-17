package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.rules.isCalling
import io.gitlab.arturbosch.detekt.rules.isNonNullCheck
import io.gitlab.arturbosch.detekt.rules.isNullCheck
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.util.getType
import org.jetbrains.kotlin.types.isNullable

/**
 * Turn on this rule to flag `find` calls for null check that can be replaced with a `any` or `none` call.
 *
 * <noncompliant>
 * listOf(1, 2, 3).find { it == 4 } != null
 * listOf(1, 2, 3).find { it == 4 } == null
 * </noncompliant>
 *
 * <compliant>
 * listOf(1, 2, 3).any { it == 4 }
 * listOf(1, 2, 3).none { it == 4 }
 * </compliant>
 */
@RequiresTypeResolution
@ActiveByDefault(since = "1.21.0")
class UseAnyOrNoneInsteadOfFind(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue(
        "UseAnyOrNoneInsteadOfFind",
        Severity.Style,
        "Use `any` or `none` instead of `find` and `null` checks.",
        Debt.FIVE_MINS
    )

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (bindingContext == BindingContext.EMPTY) return

        val functionName = expression.calleeExpression?.text ?: return
        val qualifiedExpression = expression.getStrictParentOfType<KtQualifiedExpression>()
        if (qualifiedExpression?.receiverExpression?.getType(bindingContext)?.isNullable() == true) {
            return
        }
        val qualifiedOrThis = qualifiedExpression ?: expression
        val binary = qualifiedOrThis.getStrictParentOfType<KtBinaryExpression>()?.takeIf {
            it.left == qualifiedOrThis || it.right == qualifiedOrThis
        } ?: return
        if (!expression.isCalling(functionFqNames, bindingContext)) return
        val replacement = when {
            binary.isNonNullCheck() -> "any"
            binary.isNullCheck() -> "none"
            else -> return
        }
        val message = "Use '$replacement' instead of '$functionName'"
        report(CodeSmell(issue, Entity.from(expression), message))
    }

    companion object {
        private val functionNames = listOf("find", "firstOrNull", "lastOrNull")
        private val functionFqNames =
            listOf("kotlin.collections", "kotlin.sequences", "kotlin.text").flatMap { packageName ->
                functionNames.map { functionName -> FqName("$packageName.$functionName") }
            }
    }
}
