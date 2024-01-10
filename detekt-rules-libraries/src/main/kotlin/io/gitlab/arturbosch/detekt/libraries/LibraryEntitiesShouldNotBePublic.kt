package io.gitlab.arturbosch.detekt.libraries

import io.gitlab.arturbosch.detekt.api.ActiveByDefault
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.psiUtil.isPublic

/**
 * Library typealias and classes should be internal or private.
 *
 * <noncompliant>
 * // code from a library
 * class A
 * </noncompliant>
 *
 * <compliant>
 * // code from a library
 * internal class A
 * </compliant>
 */
@ActiveByDefault(since = "1.16.0")
class LibraryEntitiesShouldNotBePublic(config: Config) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        "Library classes should not be public.",
    )

    override fun visitClass(klass: KtClass) {
        if (klass.isInner()) {
            return
        }

        if (klass.isPublic) {
            report(CodeSmell(issue, Entity.from(klass), "Class ${klass.nameAsSafeName} should not be public"))
        }
    }

    override fun visitTypeAlias(typeAlias: KtTypeAlias) {
        if (typeAlias.isPublic) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(typeAlias),
                    "TypeAlias ${typeAlias.nameAsSafeName} should not be public"
                )
            )
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        if (function.isTopLevel && function.isPublic) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(function),
                    "Top level function ${function.nameAsSafeName} should not be public"
                )
            )
        }
    }
}
