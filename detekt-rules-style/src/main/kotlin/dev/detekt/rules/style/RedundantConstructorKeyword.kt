package dev.detekt.rules.style

import com.intellij.psi.PsiComment
import com.intellij.psi.PsiWhiteSpace
import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.siblings

/**
 * This rule checks for redundant constructor keywords.
 *
 * <noncompliant>
 * data class Foo constructor(val foo: Int)
 * </noncompliant>
 *
 * <compliant>
 * data class Foo(val foo: Int)
 *
 * data class Bar private constructor(val bar: String) {
 *     constructor(bar: Int): this("$foo")
 * }
 * </compliant>
 */
@ActiveByDefault(since = "2.0.0")
class RedundantConstructorKeyword(config: Config) : Rule(
    config,
    "Redundant `constructor` modifier can be removed."
) {

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        super.visitPrimaryConstructor(constructor)

        if (constructor.containingClassOrObject is KtClass &&
            constructor.hasConstructorKeyword() &&
            constructor.hasNoModifier()
        ) {
            report(
                Finding(
                    Entity.from(constructor),
                    message = "The `constructor` keyword on ${constructor.name} is redundant and should be removed."
                )
            )
        }
    }

    private fun KtPrimaryConstructor.hasNoModifier() = modifierList == null && !hasPreviousComment()

    private fun KtPrimaryConstructor.hasPreviousComment(): Boolean =
        siblings(
            forward = false,
            withItself = false
        ).takeWhile { it is PsiComment || it is PsiWhiteSpace }.any { it is PsiComment }
}
