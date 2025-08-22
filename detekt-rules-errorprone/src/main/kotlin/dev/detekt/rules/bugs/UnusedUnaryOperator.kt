package dev.detekt.rules.bugs

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiWhiteSpace
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.psiUtil.leaves
import org.jetbrains.kotlin.psi.psiUtil.parents

/**
 * Detects unused unary operators.
 *
 * <noncompliant>
 * val x = 1 + 2
 *     + 3 + 4
 * println(x) // 3
 * </noncompliant>
 *
 * <compliant>
 * val x = 1 + 2 + 3 + 4
 * println(x) // 10
 * </compliant>
 *
 */
@ActiveByDefault(since = "1.21.0")
class UnusedUnaryOperator(config: Config) :
    Rule(
        config,
        "This unary operator is unused."
    ),
    RequiresAnalysisApi {

    @Suppress("ReturnCount")
    override fun visitPrefixExpression(expression: KtPrefixExpression) {
        super.visitPrefixExpression(expression)

        if (expression.baseExpression == null) return
        val operationToken = expression.operationToken
        if (operationToken != KtTokens.PLUS && operationToken != KtTokens.MINUS) return

        if (expression.node.leaves(forward = false)
                .takeWhile { it is PsiWhiteSpace || it is PsiComment }
                .none { it is PsiWhiteSpace && it.textContains('\n') }
        ) {
            return
        }

        analyze(expression) {
            val parentOrSelf = expression.parentBinaryExpressionOrThis()
            if (parentOrSelf.isUsedAsExpression) return
            if (expression.operationReference.mainReference.resolveToSymbol() != null) return
            val message = "This '${parentOrSelf.text}' is not used"
            report(Finding(Entity.from(expression), message))
        }
    }

    private fun KtExpression.parentBinaryExpressionOrThis(): KtExpression =
        parents.takeWhile { it is KtBinaryExpression }.lastOrNull() as? KtBinaryExpression ?: this
}
