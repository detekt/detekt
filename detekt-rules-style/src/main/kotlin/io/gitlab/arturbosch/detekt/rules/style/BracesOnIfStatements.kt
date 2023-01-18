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
        if (expression.parent.parent is KtIfExpression) return

        val branches: List<KtExpression> = walk(expression)
        validate(branches, policy(expression))
    }

    private fun walk(expression: KtExpression): List<KtExpression> {
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

    private fun validate(list: List<KtExpression>, policy: BracePolicy) {
        when (policy) {
            BracePolicy.Always -> {
                if (!list.all { hasBraces(it) }) {
                    val violator = list.first { !hasBraces(it) }
                    report(violator, policy)
                }
            }

            BracePolicy.Never -> {
                if (!list.all { !hasBraces(it) }) {
                    val violator = list.first { hasBraces(it) }
                    report(violator, policy)
                }
            }

            BracePolicy.Consistent -> {
                val reference = hasBraces(list.first())
                if (!list.all { hasBraces(it) == reference }) {
                    val violator = list.first { hasBraces(it) != reference }
                    report(violator, policy)
                }
            }
        }
    }

    private fun report(violator: KtExpression, policy: BracePolicy) {
        val iff = violator.parent.parent as KtIfExpression
        val reported = when {
            iff.then === violator -> iff.ifKeyword
            iff.`else` === violator && isMultiStatement(violator) -> iff.ifKeyword
            iff.`else` === violator -> iff.elseKeyword
            else -> error("Violating element ($violator) is not part of this if: $iff")
        }
        report(CodeSmell(issue, Entity.from(reported ?: violator), issue.description + ": $policy"))
    }

    private fun isMultiStatement(expression: KtExpression) =
        expression is KtBlockExpression && expression.statements.size > 1

    private fun policy(expression: KtExpression): BracePolicy =
        if (expression.textContains('\n')) multiLine else singleLine

    private fun hasBraces(expression: KtExpression): Boolean = expression is KtBlockExpression

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
            fun getValue(arg: String): BracePolicy =
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
