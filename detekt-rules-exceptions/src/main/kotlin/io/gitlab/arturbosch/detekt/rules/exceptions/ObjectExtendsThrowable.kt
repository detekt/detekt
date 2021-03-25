package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.internal.RequiresTypeResolution
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.typeUtil.isNotNullThrowable
import org.jetbrains.kotlin.types.typeUtil.supertypes

/**
 * This rule reports all `objects` including `companion objects` that extend any type of
 * `Throwable`. Throwable instances are not intended for reuse as they are stateful and contain
 * mutable information about a specific exception or error. Hence, global singleton `Throwables`
 * should be avoided.
 *
 * See https://kotlinlang.org/docs/reference/object-declarations.html#object-declarations
 * See https://kotlinlang.org/docs/reference/object-declarations.html#companion-objects
 *
 * <noncompliant>
 * object InvalidCredentialsException : Throwable()
 *
 * object BanException : Exception()
 *
 * object AuthException : RuntimeException()
 * </noncompliant>
 *
 * <compliant>
 * class InvalidCredentialsException : Throwable()
 *
 * class BanException : Exception()
 *
 * class AuthException : RuntimeException()
 * </compliant>
 *
 */
@RequiresTypeResolution
class ObjectExtendsThrowable(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        id = "ObjectExtendsThrowable",
        severity = Severity.CodeSmell,
        description = "An `object` should not extend and type of Throwable. Throwables are " +
            "stateful and should be instantiated only when needed for when a specific error " +
            "occurs. An `object`, being a singleton, that extends any type of Throwable " +
            "consequently introduces a global singleton exception whose instance may be " +
            "inadvertently reused from multiple places, thus introducing shared mutable " +
            "state.",
        debt = Debt.TEN_MINS
    )

    override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
        super.visitObjectDeclaration(declaration)
        if (bindingContext == BindingContext.EMPTY) return
        if (!declaration.isObjectLiteral() && declaration.isSubtypeOfThrowable()) {
            report(
                CodeSmell(
                    issue = issue,
                    entity = Entity.from(element = declaration),
                    message = "${declaration.nameAsSafeName} should be a class instead of an " +
                        "object because it is a subtype of Throwable."
                )
            )
        }
    }

    private fun KtObjectDeclaration.isSubtypeOfThrowable(): Boolean {
        return bindingContext[BindingContext.CLASS, this]
            ?.defaultType
            ?.supertypes()
            .orEmpty()
            .any { it.isNotNullThrowable() }
    }
}
