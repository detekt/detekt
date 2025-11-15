package dev.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getChildrenOfType

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
 */
@ActiveByDefault(since = "1.0.0")
class OptionalAbstractKeyword(config: Config) :
    Rule(
        config,
        "Unnecessary abstract modifier in interface detected. " +
            "This abstract modifier is unnecessary and thus can be removed."
    ) {

    override fun visitClass(klass: KtClass) {
        if (klass.isInterface()) {
            handleAbstractKeyword(klass)
            val body = klass.body
            if (body != null) {
                body.properties.forEach { handleAbstractKeyword(it) }
                body.getChildrenOfType<KtNamedFunction>().forEach { handleAbstractKeyword(it) }
            }
        }
        super.visitClass(klass)
    }

    private fun handleAbstractKeyword(dcl: KtDeclaration) {
        dcl.modifierList?.getModifier(KtTokens.ABSTRACT_KEYWORD)?.let {
            report(Finding(Entity.from(it), "The abstract keyword on this declaration is unnecessary."))
        }
    }
}
