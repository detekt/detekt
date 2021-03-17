package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtSuperTypeEntry

/**
 * An anonymous object that does nothing other than the implementation of a single method
 * can be used as a Single Abstract Method.
 *
 * See https://kotlinlang.org/docs/fun-interfaces.html
 *
 * <noncompliant>
 * object : Foo {
 *     override fun bar() {
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * Foo {
 * }
 * </compliant>
 *
 * @active since v1.17.0
 */
class SingleAbstractMethod(config: Config = Config.empty) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        Severity.Style,
        "Report anonymous objects that implement by overriding only a single method.",
        Debt.FIVE_MINS
    )

    override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
        if (declaration.isSingleAbstractMethod()) {
            report(CodeSmell(issue, Entity.from(declaration), issue.description))
        }
        super.visitObjectDeclaration(declaration)
    }

    private fun KtObjectDeclaration.isSingleAbstractMethod(): Boolean =
        name == null &&
            superTypeListEntries.size == 1 &&
            superTypeListEntries[0] is KtSuperTypeEntry &&
            hasOneNamedOverriddenMethod()

    private fun KtObjectDeclaration.hasOneNamedOverriddenMethod(): Boolean =
        declarations.size == 1 &&
            (declarations[0] as? KtNamedFunction)?.isOverride() == true
}
