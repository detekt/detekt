package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
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
class LibraryEntitiesCannotBePublic(ruleSetConfig: Config = Config.empty) : Rule(ruleSetConfig) {

    override fun visitCondition(root: KtFile): Boolean = super.visitCondition(root) && filters != null

    override val issue: Issue = Issue(
        "ClassCannotBePublic",
        Severity.Style,
        "Library class cannot be public",
        Debt.FIVE_MINS
    )

    override fun visitClass(klass: KtClass) {
        if (klass.isInner()) {
            return
        }

        if (klass.isPublic) {
            report(CodeSmell(issue, Entity.from(klass), "Class ${klass.nameAsSafeName} cannot be public"))
        }
    }

    override fun visitTypeAlias(typeAlias: KtTypeAlias) {
        if (typeAlias.isPublic) {
            report(CodeSmell(issue, Entity.from(typeAlias), "TypeAlias ${typeAlias.nameAsSafeName} cannot be public"))
        }
    }
}
