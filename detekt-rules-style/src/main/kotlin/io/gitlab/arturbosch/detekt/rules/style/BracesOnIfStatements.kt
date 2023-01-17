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
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * This rule detects `if` statements which do not comply with the specified rules.
 * Keeping braces consistent would improve readability and avoid possible errors.
 *
 * SingleLine if-statement has no '\n':
 * if (a) b else c
 * MultiLine if-statement has at least one '\n':
 * if (a) b
 * else c
 *
 * <noncompliant>
 * // singleLine = 'never'
 * if (a) { b } else { c }
 * if (a) { b } else c
 * // multiLine = 'never'
 * if (a) { b }
 * else { c }
 *
 * // singleLine = 'always'
 * if (a) b else c
 * if (a) { b } else c
 * // multiLine = 'always'
 * if (a) { b }
 * else c
 *
 * // singleLine = 'consistent'
 * if (a) b else { c }
 * // multiLine = 'consistent'
 * if (a) b
 * else { c }
 * </noncompliant>
 *
 * <compliant>
 * // singleLine = 'never'
 * if (a) b else c
 * if (a) b else { c; d; } // multi-expression
 * // multiLine = 'never'
 * if (a) b
 * else c
 *
 * // singleLine = 'always'
 * if (a) { b } else { c }
 * if (a) { b } else if (c) { d }
 * // multiLine = 'always'
 * if (a) { b }
 * else { c }
 * if (a) { b }
 * else if (c) { d }
 *
 * // singleLine = 'consistent'
 * if (a) { b } else { c }
 * if (a) b else { c; d } // multi-expression
 * // multiLine = 'consistent'
 * if (a) { b }
 * else { c }
 * if (a) b
 * else c
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
    private val singleLine: BracePolicy by config(BRACES_NEVER) { BracePolicy.getValue(it) }

    @Configuration("multi-line braces policy")
    private val multiLine: BracePolicy by config(BRACES_ALWAYS) { BracePolicy.getValue(it) }

    override fun visitIfExpression(expression: KtIfExpression) {
        super.visitIfExpression(expression)

        val policy = policy(expression)

        val thenExpression = expression.then ?: return
        if (!isExpressionCompliant(policy, thenExpression)) {
            report(CodeSmell(issue, Entity.from(expression.ifKeyword), issue.description))
        }

        val elseExpression = expression.`else` ?: return
        if (!shouldSkipElse(elseExpression) && !isExpressionCompliant(policy, elseExpression)) {
            report(CodeSmell(issue, Entity.from(expression.elseKeyword ?: elseExpression), issue.description))
        }

        if (!isStatementConsistent(policy, thenExpression, elseExpression)) {
            report(CodeSmell(issue, Entity.from(expression.ifKeyword), "Inconsistent braces"))
        }
    }

    private fun policy(expression: KtExpression) = if (expression.textContains('\n')) multiLine else singleLine

    private fun hasBraces(expression: KtExpression) = expression is KtBlockExpression

    private fun isExpressionCompliant(policy: BracePolicy, expression: KtExpression): Boolean {
        if (isMultiStatement(expression)) return true

        return when (policy) {
            BracePolicy.Always -> { hasBraces(expression) }
            BracePolicy.Never -> { !hasBraces(expression) }
            BracePolicy.Consistent -> { true }
        }
    }

    private fun isStatementConsistent(
        policy: BracePolicy,
        thenExpression: KtExpression,
        elseExpression: KtExpression
    ): Boolean {
        if (shouldSkipElse(elseExpression) ||
            isMultiStatement(thenExpression) ||
            isMultiStatement(elseExpression)
        ) { return true }

        return if (policy == BracePolicy.Consistent) {
            hasBraces(thenExpression) && hasBraces(elseExpression) ||
                !hasBraces(thenExpression) && !hasBraces(elseExpression)
        } else { true }
    }

    private fun isMultiStatement(expression: KtExpression): Boolean =
        expression is KtBlockExpression && expression.statements.size > 1

    private fun shouldSkipElse(elseExpression: KtExpression): Boolean =
        elseExpression is KtIfExpression || elseExpression is KtWhenExpression

    companion object {
        private const val BRACES_ALWAYS = "always"
        private const val BRACES_NEVER = "never"
        private const val BRACES_CONSISTENT = "consistent"
    }

    enum class BracePolicy {
        Always,
        Never,
        Consistent;

        companion object {
            fun getValue(arg: String) =
                when (arg) {
                    BRACES_NEVER -> Never
                    BRACES_ALWAYS -> Always
                    BRACES_CONSISTENT -> Consistent
                    else -> error(
                        "Unknown value $arg, allowed values are: $BRACES_NEVER|$BRACES_ALWAYS|$BRACES_CONSISTENT"
                    )
                }
        }
    }
}
