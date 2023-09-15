package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.cfg.WhenChecker
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * Detects `if` and `when` statements that could be expressions.
 *
 * Refactoring the statements to expressions improves readability by reducing the return and assignment count.
 *
 * <noncompliant>
 *     when (foo) {
 *       0 -> baz = 0
 *       else -> baz = 1
 *     }
 *
 *     if (foo == 0) {
 *       return 0
 *     } else {
 *       return 1
 *     }
 * </noncompliant>
 *
 * <compliant>
 *     baz = when (foo) {
 *       0 -> 0
 *       else -> 1
 *     }
 *
 *     return if (foo == 0) {
 *       0
 *     } else {
 *       1
 *     }
 * </compliant>
 */

@RequiresTypeResolution
class StatementCouldBeExpression(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        "StatementCouldBeExpression",
        "`if` or `when` statement. This increases the assignment and return count. Use an `if` or `when` expression instead.",
        Debt.FIVE_MINS,
    )

    override fun visitIfExpression(expression: KtIfExpression) {
        if (canLiftOutOf(expression)) {
            report(CodeSmell(issue, Entity.from(expression), "'if' statement can be replaced with 'if' expression"))
        }
    }

    private fun canLiftOutOf(expression: KtIfExpression): Boolean {
        val branches = getBranches(expression)
        return isExhaustive(expression) && canLiftOut(branches)
    }

    private fun getBranches(expression: KtIfExpression): List<KtExpression> {
        val list = mutableListOf<KtExpression>()
        var current: KtExpression? = expression
        while (current is KtIfExpression) {
            current.then?.let { list.add(it) }
            // Don't add `if` because it's an `else if` which we treat as one unit.
            current.`else`?.takeIf { it !is KtIfExpression }?.let { list.add(it) }
            current = current.`else`
        }
        return list
    }

    private fun isExhaustive(expression: KtIfExpression): Boolean {
        return expression.`else` != null
    }

    override fun visitWhenExpression(expression: KtWhenExpression) {
        if (canLiftOutOf(expression)) {
            report(CodeSmell(issue, Entity.from(expression), "'when' statement can be replaced with 'when' expression"))
        }
    }

    private fun canLiftOutOf(expression: KtWhenExpression): Boolean {
        return isExhaustive(expression) && canLiftOut(expression.entries)
    }

    private fun isExhaustive(expression: KtWhenExpression): Boolean {
        val missingCases = WhenChecker.getMissingCases(expression, bindingContext)
        return missingCases.isEmpty()
    }

    private fun <T : KtElement> canLiftOut(entries: List<T>): Boolean {
        return canLiftOutReturn(entries) || canLiftOutAssignment(entries)
    }

    private fun <T : KtElement> canLiftOutReturn(entries: List<T>): Boolean {
        return entries.all { it.children[it.children.lastIndex] is KtReturnExpression }
    }

    private fun <T : KtElement> canLiftOutAssignment(entries: List<T>): Boolean {
        val lastChildrenWhichAreBinaryExpression = entries.map { it.children[it.children.lastIndex] }.filterIsInstance<KtBinaryExpression>()
        val everyLastChildrenIsBinaryExpression = lastChildrenWhichAreBinaryExpression.size == entries.size
        val lastChildrenHaveSameLhsAndAssignmentOperator =
            haveSameLhs(lastChildrenWhichAreBinaryExpression) && haveSameOperator(lastChildrenWhichAreBinaryExpression) && haveAssignmentOperator(
                lastChildrenWhichAreBinaryExpression,
            )
        return everyLastChildrenIsBinaryExpression && lastChildrenHaveSameLhsAndAssignmentOperator
    }

    private fun haveSameLhs(expressions: List<KtBinaryExpression>): Boolean {
        return expressions.isEmpty() || expressions.all { expressions[0].left?.text == it.left?.text }
    }

    private fun haveSameOperator(expressions: List<KtBinaryExpression>): Boolean {
        return expressions.isEmpty() || expressions.all { expressions[0].operationToken == it.operationToken }
    }

    private fun haveAssignmentOperator(expressions: List<KtBinaryExpression>): Boolean {
        return expressions.all { it.operationToken in KtTokens.ALL_ASSIGNMENTS }
    }
}
