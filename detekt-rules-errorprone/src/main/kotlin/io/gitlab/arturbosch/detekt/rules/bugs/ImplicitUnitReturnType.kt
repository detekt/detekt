package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.api.internal.Configuration
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.builtins.StandardNames
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.calls.util.getResolvedCall
import org.jetbrains.kotlin.types.typeUtil.isUnit

/**
 * Functions using expression statements have an implicit return type.
 * Changing the type of the expression accidentally, changes the functions return type.
 * This may lead to backward incompatibility.
 * Use a block statement to make clear this function will never return a value.
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
 */
@RequiresTypeResolution
class ImplicitUnitReturnType(config: Config) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Defect,
        "Functions using expression statements have an implicit return type. " +
            "Changing the type of the expression accidentally, changes the function return type. " +
            "This may lead to backward incompatibility. " +
            "Use a block statement to make clear this function will never return a value.",
        Debt.FIVE_MINS
    )

    @Configuration("if functions with explicit 'Unit' return type should be allowed")
    private val allowExplicitReturnType: Boolean by config(true)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (allowExplicitReturnType && function.hasDeclaredReturnType()) {
            return
        }

        val bodyExpression = function.bodyExpression
        if (bodyExpression == null || bodyExpression.isUnitExpression()) {
            return
        }

        if (function.hasImplicitUnitReturnType()) {
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

    private fun KtExpression.isUnitExpression() = text == StandardNames.FqNames.unit.shortName().asString()

    private fun KtNamedFunction.hasImplicitUnitReturnType() =
        bodyExpression.getResolvedCall(bindingContext)?.resultingDescriptor?.returnType?.isUnit() == true
}
