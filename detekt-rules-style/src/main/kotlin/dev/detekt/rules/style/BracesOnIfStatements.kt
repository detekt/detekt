package dev.detekt.rules.style

import com.intellij.psi.PsiElement
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression

/**
 * This rule detects `if` statements which do not comply with the specified rules.
 * Keeping braces consistent will improve readability and avoid possible errors.
 *
 * The available options are:
 * - `always`: forces braces on all `if` and `else` branches in the whole codebase.
 * - `consistent`: ensures that braces are consistent within each `if`-`else if`-`else` chain.
 * If there's a brace on one of the branches, all branches should have it.
 * - `necessary`: forces no braces on any `if` and `else` branches in the whole codebase
 * except where necessary for multi-statement branches.
 * - `never`: forces no braces on any `if` and `else` branches in the whole codebase.
 *
 * Single-line if-statement has no line break (\n):
 * ```kotlin
 * if (a) b else c
 * ```
 * Multi-line if-statement has at least one line break (\n):
 * ```kotlin
 * if (a) b
 * else c
 * ```
 *
 * <noncompliant>
 * // singleLine = 'never'
 * if (a) { b } else { c }
 *
 * if (a) { b } else c
 *
 * if (a) b else { c; d }
 *
 * // multiLine = 'never'
 * if (a) {
 *     b
 * } else {
 *     c
 * }
 *
 * // singleLine = 'always'
 * if (a) b else c
 *
 * if (a) { b } else c
 *
 * // multiLine = 'always'
 * if (a) {
 *     b
 * } else
 *     c
 *
 * // singleLine = 'consistent'
 * if (a) b else { c }
 * if (a) b else if (c) d else { e }
 *
 * // multiLine = 'consistent'
 * if (a)
 *     b
 * else {
 *     c
 * }
 *
 * // singleLine = 'necessary'
 * if (a) { b } else { c; d }
 *
 * // multiLine = 'necessary'
 * if (a) {
 *     b
 *     c
 * } else if (d) {
 *     e
 * } else {
 *     f
 * }
 * </noncompliant>
 *
 * <compliant>
 * // singleLine = 'never'
 * if (a) b else c
 *
 * // multiLine = 'never'
 * if (a)
 *     b
 * else
 *     c
 *
 * // singleLine = 'always'
 * if (a) { b } else { c }
 *
 * if (a) { b } else if (c) { d }
 *
 * // multiLine = 'always'
 * if (a) {
 *     b
 * } else {
 *     c
 * }
 *
 * if (a) {
 *     b
 * } else if (c) {
 *     d
 * }
 *
 * // singleLine = 'consistent'
 * if (a) b else c
 *
 * if (a) { b } else { c }
 *
 * if (a) { b } else { c; d }
 *
 * // multiLine = 'consistent'
 * if (a) {
 *     b
 * } else {
 *     c
 * }
 *
 * if (a) b
 * else c
 *
 * // singleLine = 'necessary'
 * if (a) b else { c; d }
 *
 * // multiLine = 'necessary'
 * if (a) {
 *     b
 *     c
 * } else if (d)
 *     e
 * else
 *     f
 * </compliant>
 */
class BracesOnIfStatements(config: Config) : Rule(config, "Braces do not comply with the specified policy") {

    @Configuration("single-line braces policy")
    private val singleLine: BracePolicy by config("never") { BracePolicy.getValue(it) }

    @Configuration("multi-line braces policy")
    private val multiLine: BracePolicy by config("always") { BracePolicy.getValue(it) }

    override fun visitIfExpression(expression: KtIfExpression) {
        super.visitIfExpression(expression)

        val parent = expression.parentIfCandidate()
        // Ignore `else` branches, they're handled by the initial `if`'s visit.
        // But let us process `then` branches and conditions, because they might be `if` themselves.
        if (parent is KtIfExpression && parent.`else` === expression) return

        val branches: List<KtExpression> = walk(expression)
        validate(branches, policy(expression))
    }

    private fun walk(expression: KtExpression): List<KtExpression> {
        val list = mutableListOf<KtExpression>()
        var current: KtExpression? = expression
        while (current is KtIfExpression) {
            current.then?.let { list.add(it) }
            current.`else`?.takeIf {
                // Don't add `if` because it's an `else if` which we treat as one unit.
                it !is KtIfExpression &&
                    // Don't add KtQualifiedExpression because it's `if-else` chained with other expression
                    it !is KtQualifiedExpression &&
                    // Don't add KtBinaryExpression because it's `if-else` chained with elvis or other binary expression
                    it !is KtBinaryExpression
            }?.let { list.add(it) }
            current = current.`else`
        }
        return list
    }

    private fun validate(list: List<KtExpression>, policy: BracePolicy) {
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

    private fun report(violator: KtExpression, policy: BracePolicy) {
        val iff = violator.parentIfCandidate() as KtIfExpression
        val reported = when {
            iff.then === violator -> iff.ifKeyword
            iff.`else` === violator -> iff.elseKeyword
            else -> error("Violating element (${violator.text}) is not part of this if (${iff.text})")
        }
        val message = when (policy) {
            BracePolicy.Always -> "Missing braces on this branch, add them."
            BracePolicy.Consistent -> "Inconsistent braces, make sure all branches either have or don't have braces."
            BracePolicy.Necessary -> "Extra braces exist on this branch, remove them (ignore multi-statement)."
            BracePolicy.Never -> "Extra braces exist on this branch, remove them."
        }
        report(Finding(Entity.from(reported ?: violator), message))
    }

    /**
     * Returns a potential parent of the expression, that could be a [KtIfExpression].
     * There's a double-indirection needed because the `then` and `else` branches
     * are represented as a [org.jetbrains.kotlin.psi.KtContainerNodeForControlStructureBody].
     * Also, the condition inside the `if` is in an intermediate [org.jetbrains.kotlin.psi.KtContainerNode].
     * ```
     *         if        (parent)
     *      /  |  \
     * cond  then  else  (parent)
     *  |     |     |
     * expr  expr  expr
     * ```
     * @see org.jetbrains.kotlin.KtNodeTypes.CONDITION
     * @see org.jetbrains.kotlin.KtNodeTypes.THEN
     * @see org.jetbrains.kotlin.KtNodeTypes.ELSE
     */
    private fun KtExpression.parentIfCandidate(): PsiElement? = this.parent.parent

    private fun isMultiStatement(expression: KtExpression): Boolean =
        expression is KtBlockExpression && expression.statements.size > 1

    private fun policy(expression: KtExpression): BracePolicy =
        if (expression.textContains('\n')) multiLine else singleLine

    private fun hasBraces(expression: KtExpression): Boolean = expression is KtBlockExpression

    enum class BracePolicy(val config: String) {
        Always("always"),
        Consistent("consistent"),
        Necessary("necessary"),
        Never("never"),
        ;

        companion object {
            fun getValue(arg: String): BracePolicy =
                entries.singleOrNull { it.config == arg }
                    ?: error("Unknown value $arg, allowed values are: ${entries.joinToString("|")}")
        }
    }
}
