package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.psiUtil.leaves
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.bindingContextUtil.isUsedAsExpression
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall

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
@RequiresTypeResolution
@ActiveByDefault(since = "1.21.0")
class UnusedUnaryOperator(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "This unary operator is unused.",
        Debt.FIVE_MINS
    )

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

        val parentOrSelf = expression.parentBinaryExpressionOrThis()
        if (parentOrSelf.isUsedAsExpression(bindingContext)) return

        val operatorDescriptor = expression.operationReference.getResolvedCall(bindingContext)
            ?.resultingDescriptor as? DeclarationDescriptor ?: return
        if (!KotlinBuiltIns.isUnderKotlinPackage(operatorDescriptor)) return

        val message = "This '${parentOrSelf.text}' is not used"
        report(CodeSmell(issue, Entity.from(expression), message))
    }

    private fun KtExpression.parentBinaryExpressionOrThis(): KtExpression {
        return parents.takeWhile { it is KtBinaryExpression }.lastOrNull() as? KtBinaryExpression ?: this
    }
}
