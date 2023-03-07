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
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * This rule detects `when` statements which do not comply with the specified policy.
 * Keeping braces consistent will improve readability and avoid possible errors.
 *
 * Single-line `when` statement is:
 * a `when` where each of the entries are single-line (has no line breaks `\n`).
 *
 * Multi-line `when` statement is:
 * a `when` where at least one of the entries is multi-line (has a break line `\n`)
 * or has multiple statements.
 *
 * Available options are:
 *  * `never`: forces no braces on any entry.  
      _Tip_: this is very strict, it will force a simple expression, like a single function call / expression. Extracting a function for "complex" logic is one way to adhere to this policy.
 *  * `necessary`: forces no braces on any entry except where necessary for multi-statement entries.
 *  * `consistent`: ensures that braces are consistent within `when` statement.
 *    If there are braces on one of the entries, all entries should have it.
 *  * `always`: forces braces on all entries.
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
 *      2 -> f2()
 *  }
 *  // multiLine = 'necessary'
 *  when (a) {
 *      1 -> { f1() }
 *      2 -> { f2(); f3() }
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
    private val singleLine: BracePolicy by config("consistent") { BracePolicy.getValue(it) }

    @Configuration("multi-line braces policy")
    private val multiLine: BracePolicy by config("consistent") { BracePolicy.getValue(it) }

    override fun visitWhenExpression(expression: KtWhenExpression) {
        super.visitWhenExpression(expression)

        val branches: List<KtExpression> = walk(expression)
        validate(branches, policy(expression))
    }

    private fun walk(expression: KtWhenExpression): List<KtExpression> {
        return expression.entries.mapNotNull { it.expression }
    }

    private fun validate(branches: List<KtExpression>, policy: BracePolicy) {
        val violators = when (policy) {
            BracePolicy.Always -> {
                list.filter { !hasBraces(it) }
            }

            BracePolicy.Necessary -> {
                list.filter { !isMultiStatement(it) && hasBraces(it) }
            }

            BracePolicy.Never -> {
                list.filter { hasBraces(it) }
            }

            BracePolicy.Consistent -> {
                val braces = list.count { hasBraces(it) }
                val noBraces = list.count { !hasBraces(it) }
                if (braces != 0 && noBraces != 0) {
                    list.take(1)
                } else {
                    emptyList()
                }
            }
        }
        violators.forEach { report(it, policy) }
    }

    private fun hasBraces(expression: KtExpression): Boolean =
        expression is KtBlockExpression

    private fun KtExpression.parentCandidate(): PsiElement? =
        this.parent.parent

    private fun isMultiStatement(expression: KtExpression): Boolean =
        expression is KtBlockExpression && expression.statements.size > 1

    private fun policy(expression: KtWhenExpression): BracePolicy {
        val multiLineCandidate = expression.entries.firstOrNull { entry ->
            entry.text.substringAfter("->").contains('\n') ||
                entry.expression?.let { isMultiStatement(it) } ?: false
        }
        return if (multiLineCandidate != null) multiLine else singleLine
    }

    private fun report(violator: KtExpression, policy: BracePolicy) {
        val parent = violator.parentCandidate() as KtWhenExpression
        val entries = parent.entries.mapNotNull { it.expression }
        val reported = when {
            violator in entries && policy == BracePolicy.Consistent -> parent.whenKeyword
            violator in entries -> violator
            else -> error("Violating element (${violator.text}) is not part of this 'when' (${parent.text})")
        }
        val message = when (policy) {
            BracePolicy.Always -> "Missing braces on this branch, add them."
            BracePolicy.Consistent -> "Inconsistent braces, make sure all branches either have or don't have braces."
            BracePolicy.Necessary -> "Extra braces exist on this branch, remove them (ignore multi-statement)."
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
