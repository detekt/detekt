package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.cfg.WhenChecker
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.siblings
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.bindingContextUtil.isUsedAsExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getType
import org.jetbrains.kotlin.types.typeUtil.isUnit
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

/**
 * It is not necessary to define a return type of `Unit` on functions or to specify a lone Unit statement.
 * This rule detects and reports instances where the `Unit` return type is specified on functions and the occurrences
 * of a lone Unit statement.
 *
 * <noncompliant>
 * fun foo(): Unit {
 *     return Unit 
 * }
 * fun foo() = Unit
 *
 * fun doesNothing() {
 *     Unit
 * }
 * </noncompliant>
 *
 * <compliant>
 * fun foo() { }
 *
 * // overridden no-op functions are allowed
 * override fun foo() = Unit
 * </compliant>
 */
class OptionalUnit(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
            javaClass.simpleName,
            Severity.Style,
            "Return type of 'Unit' is unnecessary and can be safely removed.",
            Debt.FIVE_MINS)

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.hasDeclaredReturnType()) {
            checkFunctionWithExplicitReturnType(function)
        } else if (!function.isOverride()) {
            checkFunctionWithInferredReturnType(function)
        }
        super.visitNamedFunction(function)
    }

    override fun visitBlockExpression(expression: KtBlockExpression) {
        val statements = expression.statements
        val lastStatement = statements.lastOrNull() ?: return
        statements
                .filter {
                    when {
                        it !is KtNameReferenceExpression || it.text != UNIT -> false
                        it != lastStatement || bindingContext == BindingContext.EMPTY -> true
                        !it.isUsedAsExpression(bindingContext) -> true
                        else -> {
                            val prev =
                                it.siblings(forward = false, withItself = false).firstIsInstanceOrNull<KtExpression>()
                            prev?.getType(bindingContext)?.isUnit() == true && prev.canBeUsedAsValue()
                        }
                    }
                }
                .onEach {
                    report(CodeSmell(issue, Entity.from(expression),
                            "A single Unit expression is unnecessary and can safely be removed"))
                }
        super.visitBlockExpression(expression)
    }

    private fun KtExpression.canBeUsedAsValue(): Boolean {
        return when (this) {
            is KtIfExpression -> {
                val elseExpression = `else`
                if (elseExpression is KtIfExpression) elseExpression.canBeUsedAsValue() else elseExpression != null
            }
            is KtWhenExpression ->
                entries.lastOrNull()?.elseKeyword != null || WhenChecker.getMissingCases(this, bindingContext).isEmpty()
            else ->
                true
        }
    }

    private fun checkFunctionWithExplicitReturnType(function: KtNamedFunction) {
        val typeReference = function.typeReference
        val typeElementText = typeReference?.typeElement?.text
        if (typeElementText == UNIT) {
            report(CodeSmell(issue, Entity.from(typeReference), createMessage(function)))
        }
    }

    private fun checkFunctionWithInferredReturnType(function: KtNamedFunction) {
        val referenceExpression = function.bodyExpression as? KtNameReferenceExpression
        if (referenceExpression != null && referenceExpression.text == UNIT) {
            report(CodeSmell(issue, Entity.from(referenceExpression), createMessage(function)))
        }
    }

    private fun createMessage(function: KtNamedFunction) = "The function ${function.name} " +
            "defines a return type of Unit. This is unnecessary and can safely be removed."

    companion object {
        private const val UNIT = "Unit"
    }
}
