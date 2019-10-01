package io.gitlab.arturbosch.detekt.rules.style.optional

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

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
        if (function.funKeyword == null) return
        if (isInInterface(function)) return
        if (function.hasDeclaredReturnType() && function.colon != null) {
            checkFunctionWithExplicitReturnType(function)
        } else if (!function.isOverride()) {
            checkFunctionWithInferredReturnType(function)
        }
        super.visitNamedFunction(function)
    }

    override fun visitBlockExpression(expression: KtBlockExpression) {
        expression.statements
                .filter { it is KtNameReferenceExpression && it.text == UNIT }
                .onEach {
                    report(CodeSmell(issue, Entity.from(expression),
                            "A single Unit expression is unnecessary and can safely be removed"))
                }
        super.visitBlockExpression(expression)
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

    private fun isInInterface(function: KtNamedFunction) =
        function.getStrictParentOfType<KtClass>()?.isInterface() ?: false

    private fun createMessage(function: KtNamedFunction) = "The function ${function.name} " +
            "defines a return type of Unit. This is unnecessary and can safely be removed."

    companion object {
        private const val UNIT = "Unit"
    }
}
