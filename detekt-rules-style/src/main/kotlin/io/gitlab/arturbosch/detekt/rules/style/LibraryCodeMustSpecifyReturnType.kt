package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.checkers.ExplicitApiDeclarationChecker

/**
 * Functions/properties exposed as public APIs of a library should have an explicit return type.
 * Inferred return type can easily be changed by mistake which may lead to breaking changes.
 *
 * See also: [Kotlin 1.4 Explicit API](https://kotlinlang.org/docs/reference/whatsnew14.html#explicit-api-mode-for-library-authors)
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
 * @requiresTypeResolution
 * @active since v1.2.0
 */
class LibraryCodeMustSpecifyReturnType(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        this.javaClass.simpleName,
        Severity.Style,
        "Library functions/properties should have an explicit return type. " +
            "Inferred return type can easily be changed by mistake which may lead to breaking changes.",
        Debt.FIVE_MINS
    )

    override fun visitProperty(property: KtProperty) {
        if (bindingContext == BindingContext.EMPTY) {
            return
        }
        if (property.explicitReturnTypeRequired()) {
            report(CodeSmell(
                issue,
                Entity.atName(property),
                "Library property '${property.nameAsSafeName}' without explicit return type."
            ))
        }
        super.visitProperty(property)
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (bindingContext == BindingContext.EMPTY) {
            return
        }
        if (function.explicitReturnTypeRequired()) {
            report(
                CodeSmell(
                    issue,
                    Entity.atName(function),
                    "Library function '${function.nameAsSafeName}' without explicit return type."
                )
            )
        }
        super.visitNamedFunction(function)
    }

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
