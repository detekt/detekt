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
import io.gitlab.arturbosch.detekt.rules.hasImplicitUnitReturnType
import io.gitlab.arturbosch.detekt.rules.isUnitExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * Reports the usage of implicit return type in a function declaration.
 * Implicit return type is problematic as changing the type of the expression accidentally, changes the function return
 * type. This may lead to backward incompatibility. Specify a return type in the function to prevent unintentional
 * type changes",
 *
 * <noncompliant>
 * fun getCalculationResult() = Calculator.calculate()
 * // allowOmitUnit = 'false'
 * fun log(msg) = println(msg)
 * </noncompliant>
 *
 * <compliant>
 * fun getCalculationResult(): Double = Calculator.calculate()
 * // allowOmitUnit = 'true'
 * fun log(msg) = println(msg)
 * </compliant>
 *
 */
@RequiresTypeResolution
class ExplicitlyDefineReturn(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "Functions using expression statements without any return type declaration have an implicit return " +
            "type. Changing the type of the expression accidentally, changes the function return type. " +
            "This may lead to backward incompatibility. " +
            "Specify a return type in the function to prevent unintentional type changes",
        Debt.FIVE_MINS
    )

    @Configuration("if functions with `Unit` return type should be allowed without return type declaration")
    private val allowOmitUnit: Boolean by config(true)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)

        if (function.bodyBlockExpression != null) {
            return
        }

        if (allowOmitUnit && function.hasImplicitUnitReturnType(bindingContext)) {
            return
        }

        val bodyExpression = function.bodyExpression
        if (bodyExpression == null || bodyExpression.isUnitExpression()) {
            return
        }

        if (function.hasDeclaredReturnType().not()) {
            val message = "`${function.name}` has the implicit return type." +
                " Prefer specify the return type explicitly"
            report(
                CodeSmell(
                    issue,
                    Entity.atName(function),
                    message
                )
            )
        }
    }
}
