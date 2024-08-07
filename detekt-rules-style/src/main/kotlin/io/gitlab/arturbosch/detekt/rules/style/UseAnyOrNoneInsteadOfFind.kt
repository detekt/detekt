package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.isCalling
import io.gitlab.arturbosch.detekt.rules.isNonNullCheck
import io.gitlab.arturbosch.detekt.rules.isNullCheck
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

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
@ActiveByDefault(since = "1.21.0")
class UseAnyOrNoneInsteadOfFind(config: Config) :
    Rule(
        config,
        "Use `any` or `none` instead of `find` and `null` checks."
    ),
    RequiresTypeResolution {
    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val functionName = expression.calleeExpression?.text ?: return
        val qualifiedOrThis = expression.getStrictParentOfType<KtQualifiedExpression>() ?: expression
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
        report(CodeSmell(Entity.from(expression), message))
    }

    companion object {
        private val functionNames = listOf("find", "firstOrNull", "lastOrNull")
        private val functionFqNames =
            listOf("kotlin.collections", "kotlin.sequences", "kotlin.text").flatMap { packageName ->
                functionNames.map { functionName -> FqName("$packageName.$functionName") }
            }
    }
}
