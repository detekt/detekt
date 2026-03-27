package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isCalling
import dev.detekt.psi.isNonNullCheck
import dev.detekt.psi.isNullCheck
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
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
    RequiresAnalysisApi {

    @Suppress("ReturnCount")
    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)

        val functionName = expression.calleeExpression?.text ?: return
        val qualifiedOrThis = expression.getStrictParentOfType<KtQualifiedExpression>() ?: expression
        val binary = qualifiedOrThis.getStrictParentOfType<KtBinaryExpression>()?.takeIf {
            it.left == qualifiedOrThis || it.right == qualifiedOrThis
        } ?: return
        if (!expression.isCalling(functionIds)) return
        val replacement = when {
            binary.isNonNullCheck() -> "any"
            binary.isNullCheck() -> "none"
            else -> return
        }
        val message = "Use '$replacement' instead of '$functionName'"
        report(Finding(Entity.from(expression), message))
    }

    companion object {
        private val functionNames = listOf("find", "firstOrNull", "lastOrNull")
        private val functionIds =
            listOf(
                StandardClassIds.BASE_COLLECTIONS_PACKAGE,
                StandardClassIds.BASE_SEQUENCES_PACKAGE,
                StandardClassIds.BASE_TEXT_PACKAGE,
            ).flatMap { pkg ->
                functionNames.map { CallableId(pkg, Name.identifier(it)) }
            }
    }
}
