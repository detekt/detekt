package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.com.intellij.psi.PsiComment
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
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
class RedundantConstructorKeyword(config: Config) : Rule(config) {
    override val issue = Issue(
        javaClass.simpleName,
        "Redundant `constructor` modifier can be removed.",
    )

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        super.visitPrimaryConstructor(constructor)

        if (constructor.containingClassOrObject is KtClass &&
            constructor.hasConstructorKeyword() &&
            constructor.hasNoModifier()
        ) {
            report(
                CodeSmell(
                    issue,
                    Entity.from(constructor),
                    message = "The `constructor` keyword on ${constructor.name} is redundant and should be removed."
                )
            )
        }
    }

    private fun KtPrimaryConstructor.hasNoModifier() = modifierList == null && !hasPreviousComment()

    private fun KtPrimaryConstructor.hasPreviousComment(): Boolean = siblings(
        forward = false,
        withItself = false
    ).takeWhile { it is PsiComment || it is PsiWhiteSpace }.any { it is PsiComment }
}
