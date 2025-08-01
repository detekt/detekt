package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
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
 *      1 -> { f1() } // Not allowed.
 *      2 -> f2()
 *  }
 *  // multiLine = 'never'
 *  when (a) {
 *      1 -> { // Not allowed.
 *          f1()
 *      }
 *      2 -> f2()
 *  }
 *  // singleLine = 'necessary'
 *  when (a) {
 *      1 -> { f1() } // Unnecessary braces.
 *      2 -> f2()
 *  }
 *  // multiLine = 'necessary'
 *  when (a) {
 *      1 -> { // Unnecessary braces.
 *          f1()
 *      }
 *      2 -> f2()
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
 *          f1() // Missing braces.
 *      2 -> {
 *          f2()
 *          f3()
 *      }
 *  }
 *
 *  // singleLine = 'always'
 *  when (a) {
 *      1 -> { f1() }
 *      2 -> f2() // Missing braces.
 *  }
 *  // multiLine = 'always'
 *  when (a) {
 *      1 ->
 *          f1() // Missing braces.
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
 *      2 -> { f2(); f3() } // Necessary braces because of multiple statements.
 *  }
 *  // multiLine = 'necessary'
 *  when (a) {
 *      1 ->
 *          f1()
 *      2 -> { // Necessary braces because of multiple statements.
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
class BracesOnWhenStatements(config: Config) : Rule(
    config,
    "Braces do not comply with the specified policy"
) {

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
                branches.filter { it.hasNoBraces() }
            }

            BracePolicy.Necessary -> {
                branches.filter { !it.isMultiStatement() && it.hasUnnecessaryBraces() }
            }

            BracePolicy.Never -> {
                branches.filter { it.hasUnnecessaryBraces() }
            }

            BracePolicy.Consistent -> {
                val braces = branches.count { it.hasUnnecessaryBraces() }
                val noBraces = branches.count { it.hasNoBraces() }
                if (braces != 0 && noBraces != 0) {
                    branches.take(1)
                } else {
                    emptyList()
                }
            }
        }
        violators.forEach { report(it, policy) }
    }

    private fun KtWhenEntry.hasNoBraces(): Boolean = expression !is KtBlockExpression

    @Suppress("ReturnCount")
    private fun KtWhenEntry.hasUnnecessaryBraces(): Boolean {
        val expression = this.expression

        if (expression !is KtBlockExpression) return false
        val statements = expression.statements.ifEmpty { return false }

        val singleLambda = statements.singleOrNull() as? KtLambdaExpression
        return singleLambda == null || singleLambda.functionLiteral.arrow != null
    }

    private fun KtWhenEntry.isMultiStatement(): Boolean =
        expression.let { it is KtBlockExpression && it.statements.size > 1 }

    private fun policy(expression: KtWhenExpression): BracePolicy {
        val isMultiLine = expression.entries.any { branch ->
            requireNotNull(branch.arrow) { "When branch ${branch.text} has no arrow!" }
                .siblings(forward = true, withItself = false)
                .any { it.textContains('\n') }
        }
        return if (isMultiLine) multiLine else singleLine
    }

    private fun report(violator: KtWhenEntry, policy: BracePolicy) {
        val reported = when (policy) {
            BracePolicy.Consistent -> (violator.parent as KtWhenExpression).whenKeyword
            BracePolicy.Always,
            BracePolicy.Necessary,
            BracePolicy.Never,
            -> requireNotNull(violator.arrow) { "When branch ${violator.text} has no arrow!" }
        }
        report(Finding(Entity.from(reported), policy.message))
    }

    enum class BracePolicy(val config: String, val message: String) {
        Always("always", "Missing braces on this branch, add them."),
        Consistent("consistent", "Inconsistent braces, make sure all branches either have or don't have braces."),
        Necessary("necessary", "Extra braces exist on this branch, remove them."),
        Never("never", "Extra braces exist on this branch, remove them."),
        ;

        companion object {
            fun getValue(arg: String): BracePolicy =
                entries.singleOrNull { it.config == arg }
                    ?: error("Unknown value $arg, allowed values are: ${entries.joinToString("|")}")
        }
    }
}
