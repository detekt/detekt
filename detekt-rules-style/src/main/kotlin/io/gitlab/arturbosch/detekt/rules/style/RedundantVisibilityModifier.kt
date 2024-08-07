package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.isInternal
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.config.AnalysisFlags
import org.jetbrains.kotlin.config.ExplicitApiMode
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * This rule checks for redundant visibility modifiers.
 * One exemption is the
 * [explicit API mode](https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors)
 * In this mode, the visibility modifier should be defined explicitly even if it is public.
 * Hence, the rule ignores the visibility modifiers in explicit API mode.
 *
 * <noncompliant>
 * public interface Foo { // public per default
 *
 *     public fun bar() // public per default
 * }
 * </noncompliant>
 *
 * <compliant>
 * interface Foo {
 *
 *     fun bar()
 * }
 * </compliant>
 */
class RedundantVisibilityModifier(config: Config) : Rule(
    config,
    "Redundant visibility modifiers detected, which can be safely removed."
) {
    private val classVisitor = ClassVisitor()
    private val childrenVisitor = ChildrenVisitor()

    private fun KtModifierListOwner.isExplicitlyPublicNotOverridden() = isExplicitlyPublic() && !isOverride()

    private fun KtModifierListOwner.isExplicitlyPublic() = this.hasModifier(KtTokens.PUBLIC_KEYWORD)

    /**
     * Explicit API mode was added in Kotlin 1.4
     * It prevents libraries' authors from making APIs public unintentionally.
     * In this mode, the visibility modifier should be defined explicitly even if it is public.
     * See: https://kotlinlang.org/docs/whatsnew14.html#explicit-api-mode-for-library-authors
     */
    private fun isExplicitApiModeActive(): Boolean {
        val flag = compilerResources.languageVersionSettings.getFlag(AnalysisFlags.explicitApiMode)
        return flag != ExplicitApiMode.DISABLED
    }

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        if (!isExplicitApiModeActive()) {
            file.declarations.forEach {
                it.accept(classVisitor)
                it.acceptChildren(childrenVisitor)
            }
        }
    }

    override fun visitDeclaration(declaration: KtDeclaration) {
        super.visitDeclaration(declaration)
        if (
            declaration.isInternal() &&
            declaration.containingClassOrObject?.let { it.isLocal || it.isPrivate() } == true
        ) {
            report(
                CodeSmell(
                    Entity.from(declaration),
                    "The `internal` modifier on ${declaration.name} is redundant and should be removed."
                )
            )
        }
    }

    private inner class ClassVisitor : DetektVisitor() {
        override fun visitClass(klass: KtClass) {
            super.visitClass(klass)
            if (klass.isExplicitlyPublic()) {
                report(
                    CodeSmell(
                        Entity.atName(klass),
                        message = "${klass.name} is explicitly marked as public. " +
                            "Public is the default visibility for classes. The public modifier is redundant."
                    )
                )
            }
        }
    }

    private inner class ChildrenVisitor : DetektVisitor() {
        override fun visitNamedFunction(function: KtNamedFunction) {
            super.visitNamedFunction(function)
            if (function.isExplicitlyPublicNotOverridden()) {
                report(
                    CodeSmell(
                        Entity.atName(function),
                        message = "${function.name} is explicitly marked as public. " +
                            "Functions are public by default so this modifier is redundant."
                    )
                )
            }
        }

        override fun visitProperty(property: KtProperty) {
            super.visitProperty(property)
            if (property.isExplicitlyPublicNotOverridden()) {
                report(
                    CodeSmell(
                        Entity.atName(property),
                        message = "${property.name} is explicitly marked as public. " +
                            "Properties are public by default so this modifier is redundant."
                    )
                )
            }
        }
    }
}
