package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isAbstract
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * This rule reports `abstract` modifiers which are unnecessary and can be removed.
 *
 * <noncompliant>
 * abstract interface Foo { // abstract keyword not needed
 *
 *     abstract fun x() // abstract keyword not needed
 *     abstract var y: Int // abstract keyword not needed
 * }
 * </noncompliant>
 *
 * <compliant>
 * interface Foo {
 *
 *     fun x()
 *     var y: Int
 * }
 * </compliant>
 *
 * @active since v1.0.0
 */
class OptionalAbstractKeyword(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(javaClass.simpleName, Severity.Style,
            "Unnecessary abstract modifier in interface", Debt.FIVE_MINS)

    override fun visitClass(klass: KtClass) {
        if (klass.isInterface()) {
            handleAbstractKeyword(klass)
            val body = klass.body
            if (body != null) {
                body.properties.forEach { handleAbstractKeyword(it) }
                body.children.filterIsInstance<KtNamedFunction>().forEach { handleAbstractKeyword(it) }
            }
        }
        super.visitClass(klass)
    }

    internal fun handleAbstractKeyword(dcl: KtDeclaration) {
        if (dcl.isAbstract()) {
            report(CodeSmell(issue, Entity.from(dcl), "The abstract keyword on this declaration is unnecessary."))
        }
    }
}
