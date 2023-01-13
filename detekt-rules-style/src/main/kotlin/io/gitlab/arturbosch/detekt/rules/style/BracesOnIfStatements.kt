package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.siblings

/**
 * This rule detects `if` statements which do not comply with the specified rules.
 * Keeping braces consistent would improve readability and avoid possible errors.
 *
 * <noncompliant>
 * val i = 1
 * if (i > 0)
 *    println(i)
 * else {
 *    println(-i)
 * }
 * </noncompliant>
 *
 * <compliant>
 * || singleLine = 'never' multiLine = 'always'
 * val x = if (condition) 5 else 4
 * val x = if (condition) {
 *     5
 * } else {
 *     4
 * }
 * </compliant>
 */
class BracesOnIfStatements(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Braces do not comply with the specified policy",
        Debt.FIVE_MINS
    )

    @Configuration("single-line braces policy")
    private val singleLine: String by config(BRACES_NEVER)

    @Configuration("multi-line braces policy")
    private val multiLine: String by config(BRACES_ALWAYS)

    override fun visitIfExpression(expression: KtIfExpression) {
        super.visitIfExpression(expression)

        val isMultiline = isMultiline(expression.ifKeyword)

        val thenExpression = expression.then ?: return
        if (!isCompliant(thenExpression, isMultiline)) {
            report(CodeSmell(issue, Entity.from(expression.ifKeyword), issue.description))
        }

        val elseExpression = expression.`else` ?: return
        if (!shouldSkip(elseExpression) && !isCompliant(elseExpression, isMultiline)) {
            report(CodeSmell(issue, Entity.from(expression.elseKeyword ?: elseExpression), issue.description))
        }

        if (!isConsistent(thenExpression, elseExpression, isMultiline)) {
            report(CodeSmell(issue, Entity.from(expression.ifKeyword), "Inconsistent braces"))
        }
    }

    private fun isCompliant(expression: KtExpression, isMultiline: Boolean): Boolean = when {
        isMultiStatement(expression) -> {
            true
        }

        isMultiline && (multiLine == BRACES_ALWAYS) || !isMultiline && (singleLine == BRACES_ALWAYS) -> {
            expression is KtBlockExpression
        }

        isMultiline && (multiLine == BRACES_NEVER) || !isMultiline && (singleLine == BRACES_NEVER) -> {
            expression !is KtBlockExpression
        }

        else -> {
            true
        }
    }

    private fun isConsistent(
        thenExpression: KtExpression,
        elseExpression: KtExpression,
        isMultiline: Boolean
    ): Boolean = when {
        shouldSkip(elseExpression) -> {
            true
        }

        isMultiline && (multiLine == BRACES_CONSISTENT) || !isMultiline && (singleLine == BRACES_CONSISTENT) -> {
            thenExpression is KtBlockExpression && elseExpression is KtBlockExpression ||
                thenExpression !is KtBlockExpression && elseExpression !is KtBlockExpression
        }

        else -> {
            true
        }
    }

    private fun isMultiStatement(expression: KtExpression): Boolean {
        if (expression !is KtBlockExpression) return false

        return expression
            .firstChild
            .siblings(forward = true, withItself = false)
            .count { it is KtExpression } > 0
    }

    private fun isMultiline(element: PsiElement?): Boolean {
        if (element == null) return false
        return element.siblings().any { it.textContains('\n') }
    }

    private fun shouldSkip(expression: KtExpression): Boolean =
        expression is KtIfExpression || expression is KtWhenExpression

    companion object {
        private const val BRACES_ALWAYS = "always"
        private const val BRACES_NEVER = "never"
        private const val BRACES_CONSISTENT = "consistent"
    }
}
