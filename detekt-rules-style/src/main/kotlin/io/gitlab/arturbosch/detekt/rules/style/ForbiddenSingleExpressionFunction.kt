package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.types.typeUtil.isUnit
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable

/**
 * This rule detects single-expression functions with an inferred return type of `Unit` or `Unit?`.
 * The single-expression functions allows for very concise function declarations but should be avoided
 * for functions that only have side effects without returning anything meaningful because that fact may
 * not immediately be clear to the reader.
 *
 * <noncompliant>
 * fun printSomething() = println("something")
 *
 * fun maybePrintSomething(param: String?) = param?.let { println("something") }
 * </noncompliant>
 *
 * <compliant>
 * fun printSomething() {
 *     println("something")
 * }
 *
 * fun maybePrintSomething(param: String?) {
 *     param?.let { println("something") }
 * }
 * </compliant>
 *
 * @requiresTypeResolution
 */
class ForbiddenSingleExpressionFunction(config: Config = Config.empty) : Rule(config) {
    override val issue: Issue = Issue(
        "ForbiddenSingleExpressionFunction",
        Severity.Style,
        "Avoid using single-expression syntax for functions that return `Unit` or `Unit?`.",
        Debt.FIVE_MINS
    )

    override fun visitNamedFunction(function: KtNamedFunction) {

        if (isBindingContextMissing()) return
        if (function.hasBlockBody()) return
        if (function.hasDeclaredReturnType()) return
        if (function.bodyExpressionIsUnit()) return

        function.bodyExpression
            ?.getType(bindingContext)
            ?.takeIf { it.makeNotNullable().isUnit() }
            ?.also {
                report(
                    CodeSmell(
                        issue = issue,
                        entity = Entity.from(function),
                        message = "Single expression syntax should be avoided " +
                            "when the inferred return type is 'Unit' or 'Unit?'"
                    )
                )
            }
    }

    private fun KtNamedFunction.bodyExpressionIsUnit() =
        bodyExpression?.text == UNIT

    private fun isBindingContextMissing() = bindingContext == BindingContext.EMPTY

    companion object {
        private const val UNIT = "Unit"
    }
}
