package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall

/**
 * Functions using expression statements have an implicit return type.
 * Changing the type of the expression accidentally, changes the functions return type.
 * This may lead to backward incompatibility.
 * Use a block statement to make clear this function will never return a value.
 *
 * @configuration allowExplicitReturnType - if functions with explicit 'Unit' return type should be allowed
 * (default: `true`)
 *
 * <noncompliant>
 * fun errorProneUnit() = println("Hello Unit")
 * fun errorProneUnitWithParam(param: String) = param.run { println(this) }
 * fun String.errorProneUnitWithReceiver() = run { println(this) }
 * </noncompliant>
 *
 * <compliant>
 * fun blockStatementUnit() {
 *     // code
 * }
 *
 * // explicit Unit is compliant by default; can be configured to enforce block statement
 * fun safeUnitReturn(): Unit = println("Hello Unit")
 * </compliant>
 *
 * @requiresTypeResolution
 */
class ImplicitUnitReturnType(config: Config) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        """
            Functions using expression statements have an implicit return type.
            Changing the type of the expression accidentally, changes the function return type.
            This may lead to backward incompatibility.
            Use a block statement to make clear this function will never return a value.
        """.trimIndent(),
        Debt.FIVE_MINS
    )

    private val allowExplicitReturnType = valueOrDefault(ALLOW_EXPLICIT_RETURN_TYPE, true)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        if (BindingContext.EMPTY == bindingContext) {
            return
        }

        if (allowExplicitReturnType && function.hasDeclaredReturnType()) {
            return
        }

        val isExpressionBody = function.bodyExpression != null

        if (isExpressionBody && function.hasImplicitUnitReturnType()) {
            val message = buildString {
                append("'${function.name}'  has the implicit return type 'Unit'.")
                append(" Prefer using a block statement")
                if (allowExplicitReturnType) {
                    append(" or specify the return type explicitly")
                }
                append('.')
            }
            report(
                CodeSmell(
                    issue,
                    Entity.atName(function),
                    message
                )
            )
        }
    }

    private fun KtNamedFunction.hasImplicitUnitReturnType(): Boolean {
        val returnType = bodyExpression.getResolvedCall(bindingContext)
            ?.resultingDescriptor
            ?.returnType
        return returnType?.toString() == "Unit"
    }

    companion object {
        const val ALLOW_EXPLICIT_RETURN_TYPE = "allowExplicitReturnType"
    }
}
