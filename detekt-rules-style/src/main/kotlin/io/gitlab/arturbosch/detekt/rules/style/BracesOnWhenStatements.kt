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
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.siblings

/**
 * This rule detects `when` statements which do not comply with the specified policy.
 * Keeping braces consistent will improve readability and avoid possible errors.
 *
 * Single-line `when` statement is:
 * a `when` where each of the branches are single-line (has no line breaks `\n`).
 *
 * Multi-line `when` statement is:
 * a `when` where at least one of the branches is multi-line (has a break line `\n`).
 *
 * Available options are:
 *  * `never`: forces no braces on any branch.
 *  _Tip_: this is very strict, it will force a simple expression, like a single function call / expression.
 *  Extracting a function for "complex" logic is one way to adhere to this policy.
 *  * `necessary`: forces no braces on any branch except where necessary for multi-statement branches.
 *  * `consistent`: ensures that braces are consistent within `when` statement.
 *    If there are braces on one of the branches, all branches should have it.
 *  * `always`: forces braces on all branches.
 *
 *  <noncompliant>
 *  // singleLine = 'never'
 *  when (a) {
 *      1 -> { f1() }
 *      2 -> f2()
 *  }
 *  // multiLine = 'never'
 *  when (a) {
 *      1 -> {
 *          f1()
 *      }
 *      2 -> f2()
 *  }
 *  // singleLine = 'necessary'
 *  when (a) {
 *      1 -> { f1() }
 *  }
 *  // multiLine = 'necessary'
 *  when (a) {
 *      1 -> {
 *          f1()
 *      }
 *  }
 *
 *  // singleLine = 'consistent'
 *  when (a) {
 *      1 -> { f1() }
 *      2 -> f2()
 *  }
 *  // multiLine = 'consistent'
 *  when (a) {
 *      1 ->
 *          f1()
 *      2 -> {
 *          f2()
 *          f3()
 *      }
 *  }
 *
 *  // singleLine = 'always'
 *  when (a) {
 *      1 -> { f1() }
 *      2 -> f2()
 *  }
 *  // multiLine = 'always'
 *  when (a) {
 *      1 ->
 *          f1()
 *      2 -> {
 *          f2()
 *          f3()
 *      }
 *  }
 *
 *  </noncompliant>
 *
 *  <compliant>
 *  // singleLine = 'never'
 *  when (a) {
 *      1 -> f1()
 *      2 -> f2()
 *  }
 *  // multiLine = 'never'
 *  when (a) {
 *      1 ->
 *          f1()
 *      2 -> f2()
 *  }
 *  // singleLine = 'necessary'
 *  when (a) {
 *      1 -> f1()
 *      2 -> f2()
 *  }
 *  // multiLine = 'necessary'
 *  when (a) {
 *      1 ->
 *          f1()
 *      2 -> {
 *          f2()
 *          f3()
 *      }
 *  }
 *
 *  // singleLine = 'consistent'
 *  when (a) {
 *      1 -> { f1() }
 *      2 -> { f2() }
 *  }
 *  when (a) {
 *      1 -> f1()
 *      2 -> f2()
 *  }
 *  // multiLine = 'consistent'
 *  when (a) {
 *      1 -> {
 *          f1()
 *      }
 *      2 -> {
 *          f2()
 *          f3()
 *      }
 *  }
 *
 *  // singleLine = 'always'
 *  when (a) {
 *      1 -> { f1() }
 *      2 -> { f2() }
 *  }
 *  // multiLine = 'always'
 *  when (a) {
 *      1 -> {
 *          f1()
 *      }
 *      2 -> {
 *          f2()
 *          f3()
 *      }
 *  }
 *
 *  </compliant>
 */
class BracesOnWhenStatements(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Braces do not comply with the specified policy",
        Debt.FIVE_MINS
    )

    @Configuration("single-line braces policy")
    private val singleLine: BracePolicy by config("necessary") { BracePolicy.getValue(it) }

    @Configuration("multi-line braces policy")
    private val multiLine: BracePolicy by config("consistent") { BracePolicy.getValue(it) }

    override fun visitWhenExpression(expression: KtWhenExpression) {
        super.visitWhenExpression(expression)

        validate(expression.entries, policy(expression))
    }

    private fun validate(branches: List<KtWhenEntry>, policy: BracePolicy) {
        val violators = when (policy) {
            BracePolicy.Always -> {
                branches.filter { !it.hasBraces() }
            }

            BracePolicy.Necessary -> {
                branches.filter { !it.isMultiStatement() && it.hasBraces() }
            }

            BracePolicy.Never -> {
                branches.filter { it.hasBraces() }
            }

            BracePolicy.Consistent -> {
                val braces = branches.count { it.hasBraces() }
                val noBraces = branches.count { !it.hasBraces() }
                if (braces != 0 && noBraces != 0) {
                    branches.take(1)
                } else {
                    emptyList()
                }
            }
        }
        violators.forEach { report(it, policy) }
    }

    private fun KtWhenEntry.hasBraces(): Boolean = expression is KtBlockExpression

    private fun KtWhenEntry.isMultiStatement(): Boolean =
        expression.let { (it is KtBlockExpression) && (it.statements.size > 1) }

    private fun policy(expression: KtWhenExpression): BracePolicy {
        val isMultiLine = expression.entries.any { branch ->
            branch.arrow
                ?.siblings(forward = true, withItself = false)
                ?.any { it.textContains('\n') }
                ?: false
        }
        return if (isMultiLine) multiLine else singleLine
    }

    private fun report(violator: KtWhenEntry, policy: BracePolicy) {
        val parent = violator.parent as KtWhenExpression
        val reported = when {
            violator in parent.entries && policy == BracePolicy.Consistent -> parent.whenKeyword
            violator in parent.entries -> violator.arrow
            else -> error("Violating element (${violator.text}) is not part of this 'when' (${parent.text})")
        } ?: return
        val message = when (policy) {
            BracePolicy.Always -> "Missing braces on this branch, add them."
            BracePolicy.Consistent -> "Inconsistent braces, make sure all branches either have or don't have braces."
            BracePolicy.Necessary -> "Extra braces exist on this branch, remove them."
            BracePolicy.Never -> "Extra braces exist on this branch, remove them."
        }
        report(CodeSmell(issue, Entity.from(reported), message))
    }

    enum class BracePolicy(val config: String) {
        Always("always"),
        Consistent("consistent"),
        Necessary("necessary"),
        Never("never");

        companion object {
            fun getValue(arg: String): BracePolicy =
                values().singleOrNull { it.config == arg }
                    ?: error("Unknown value $arg, allowed values are: ${values().joinToString("|")}")
        }
    }
}
