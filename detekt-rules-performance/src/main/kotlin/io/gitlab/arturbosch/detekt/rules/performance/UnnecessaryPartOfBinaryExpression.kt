package io.gitlab.arturbosch.detekt.rules.performance

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression

/**
 * Unnecessary binary expression add complexity to the code and accomplish nothing. They should be removed.
 * The rule works with all binary expression included if and when condition. The rule also works with all predicates.
 * The rule verify binary expression only in case when the expression use only one type of the following
 * operators || or &&.
 *
 * <noncompliant>
 * val foo = true
 * val bar = true
 *
 * if (foo || bar || foo) {
 * }
 * </noncompliant>
 *
 * <compliant>
 * val foo = true
 * if (foo) {
 * }
 * </compliant>
 *
 */
class UnnecessaryPartOfBinaryExpression(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "UnnecessaryPartOfBinaryExpression",
        Severity.Performance,
        "Detects duplicate condition into binary expression and recommends to remove unnecessary checks",
        Debt.FIVE_MINS
    )

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)
        if (expression.parent is KtBinaryExpression) {
            return
        }

        val isOrOr = expression.getAllOperations().all { it != KtTokens.OROR }
        val isAndAnd = expression.getAllOperations().all { it != KtTokens.ANDAND }

        if (isOrOr || isAndAnd) {
            val allChildren = expression.getAllVariables().map { it.text.replace(Regex("\\s"), "") }

            if (allChildren != allChildren.distinct()) {
                report(CodeSmell(issue, Entity.from(expression), issue.description))
            }
        }
    }

    private fun KtBinaryExpression.getAllVariables(): List<KtElement> {
        return buildList {
            addAll(this@getAllVariables.left?.getVariable().orEmpty())
            addAll(this@getAllVariables.right?.getVariable().orEmpty())
        }
    }

    private fun KtExpression.getVariable(): List<KtElement> {
        return if (this is KtBinaryExpression &&
            (this.operationToken == KtTokens.OROR || this.operationToken == KtTokens.ANDAND)
        ) {
            this.getAllVariables()
        } else {
            listOf(this)
        }
    }

    private fun KtBinaryExpression.getAllOperations(): List<IElementType> {
        return buildList {
            (this@getAllOperations.left as? KtBinaryExpression)?.let {
                addAll(it.getAllOperations())
            }
            (this@getAllOperations.right as? KtBinaryExpression)?.let {
                addAll(it.getAllOperations())
            }
            add(this@getAllOperations.operationToken)
        }
    }
}
