package io.gitlab.arturbosch.detekt.libraries

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Configuration
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.RequiresTypeResolution
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.config
import io.gitlab.arturbosch.detekt.rules.hasImplicitUnitReturnType
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.checkers.ExplicitApiDeclarationChecker

/**
 * Functions/properties exposed as public APIs of a library should have an explicit return type.
 * Inferred return type can easily be changed by mistake which may lead to breaking changes.
 *
 * See also: [Kotlin 1.4 Explicit API](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors)
 *
 * <noncompliant>
 * // code from a library
 * val strs = listOf("foo, bar")
 * fun bar() = 5
 * class Parser {
 *      fun parse() = ...
 * }
 * </noncompliant>
 *
 * <compliant>
 * // code from a library
 * val strs: List<String> = listOf("foo, bar")
 * fun bar(): Int = 5
 *
 * class Parser {
 *      fun parse(): ParsingResult = ...
 * }
 * </compliant>
 *
 */
@ActiveByDefault(since = "1.2.0")
class LibraryCodeMustSpecifyReturnType(config: Config) :
    Rule(
        config,
        "Library functions/properties should have an explicit return type. " +
            "Inferred return types can easily be changed by mistake which may lead to breaking changes."
    ),
    RequiresTypeResolution {
    @Configuration("if functions with `Unit` return type should be allowed without return type declaration")
    private val allowOmitUnit: Boolean by config(false)

    override fun visitProperty(property: KtProperty) {
        if (property.explicitReturnTypeRequired()) {
            report(
                CodeSmell(
                    Entity.atName(property),
                    "Library property '${property.nameAsSafeName}' without explicit return type."
                )
            )
        }
        super.visitProperty(property)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.explicitReturnTypeRequired() && !function.isUnitOmissionAllowed()) {
            report(
                CodeSmell(
                    Entity.atName(function),
                    "Library function '${function.nameAsSafeName}' without explicit return type."
                )
            )
        }
        super.visitNamedFunction(function)
    }

    private fun KtNamedFunction.isUnitOmissionAllowed(): Boolean =
        bodyExpression?.text == "Unit" || (allowOmitUnit && this.hasImplicitUnitReturnType(bindingContext))

    private fun KtCallableDeclaration.explicitReturnTypeRequired(): Boolean =
        ExplicitApiDeclarationChecker.returnTypeCheckIsApplicable(this) &&
            ExplicitApiDeclarationChecker.returnTypeRequired(
                element = this,
                descriptor = bindingContext[BindingContext.DECLARATION_TO_DESCRIPTOR, this],
                checkForPublicApi = true,
                checkForInternal = false,
                checkForPrivate = false
            )
}
