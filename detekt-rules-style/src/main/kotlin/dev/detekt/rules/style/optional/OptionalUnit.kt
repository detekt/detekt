package dev.detekt.rules.style.optional

import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.psi.isOverride
import org.jetbrains.kotlin.analysis.api.KaIdeApi
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.singleCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.types.KaTypeParameterType
import org.jetbrains.kotlin.config.AnalysisFlags
import org.jetbrains.kotlin.config.ExplicitApiMode
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.kotlin.psi.psiUtil.isPublic
import org.jetbrains.kotlin.psi.psiUtil.siblings
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
 * fun foo() { }
 *
 * // overridden no-op functions are allowed
 * override fun foo() = Unit
 * </compliant>
 */
class OptionalUnit(config: Config) :
    Rule(
        config,
        "Return type of `Unit` is unnecessary and can be safely removed."
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        val typeReference = function.typeReference
        if (typeReference != null) {
            checkFunctionWithExplicitReturnType(function, typeReference)
        } else if (!function.isOverride()) {
            checkFunctionWithInferredReturnType(function)
        }
        super.visitNamedFunction(function)
    }

    override fun visitBlockExpression(expression: KtBlockExpression) {
        val statements = expression.statements
        val lastStatement = statements.lastOrNull() ?: return

        analyze(expression) {
            statements
                .filter {
                    when {
                        it !is KtNameReferenceExpression || it.text != UNIT -> false
                        it != lastStatement -> true
                        !it.isUsedAsExpression -> true
                        else -> {
                            val prev =
                                it.siblings(forward = false, withItself = false).firstIsInstanceOrNull<KtExpression>()
                            prev?.expressionType?.isUnitType == true && prev.canBeUsedAsValue()
                        }
                    }
                }
                .onEach {
                    report(
                        Finding(
                            Entity.from(expression),
                            "A single Unit expression is unnecessary and can safely be removed."
                        )
                    )
                }
        }

        super.visitBlockExpression(expression)
    }

    @OptIn(KaIdeApi::class)
    context(session: KaSession)
    private fun KtExpression.canBeUsedAsValue(): Boolean =
        when (this) {
            is KtIfExpression -> {
                val elseExpression = `else`
                if (elseExpression is KtIfExpression) elseExpression.canBeUsedAsValue() else elseExpression != null
            }

            is KtWhenExpression ->
                entries.lastOrNull()?.elseKeyword != null || with(session) { computeMissingCases().isEmpty() }

            else ->
                true
        }

    private fun isExplicitApiModeActive(): Boolean {
        val flag = languageVersionSettings.getFlag(AnalysisFlags.explicitApiMode)
        return flag != ExplicitApiMode.DISABLED
    }

    private fun checkFunctionWithExplicitReturnType(function: KtNamedFunction, typeReference: KtTypeReference) {
        val typeElementText = typeReference.typeElement?.text
        if (typeElementText == UNIT) {
            val initializer = function.initializer
            if (initializer?.isGenericOrNothingType() == true) return
            // case when explicit api is on so in case of expression body we need Unit
            if (initializer != null && isExplicitApiModeActive() && function.isPublic) return
            report(Finding(Entity.from(typeReference), createMessage(function)))
        }
    }

    private fun checkFunctionWithInferredReturnType(function: KtNamedFunction) {
        val referenceExpression = function.bodyExpression as? KtNameReferenceExpression
        if (referenceExpression != null && referenceExpression.text == UNIT) {
            report(Finding(Entity.from(referenceExpression), createMessage(function)))
        }
    }

    private fun createMessage(function: KtNamedFunction) =
        "The function ${function.name} defines a return type of Unit. This is unnecessary and can safely be removed."

    private fun KtExpression.isGenericOrNothingType(): Boolean {
        analyze(this) {
            val isGenericType = resolveToCall()
                ?.singleCallOrNull<KaCallableMemberCall<*, *>>()
                ?.symbol
                ?.returnType is KaTypeParameterType
            val isNothingType = expressionType?.isNothingType == true
            // Either the function initializer returns Nothing or it is a generic function
            // into which Unit is passed, but not both.
            return (isGenericType && !isNothingType) || (isNothingType && !isGenericType)
        }
    }

    companion object {
        private const val UNIT = "Unit"
    }
}
