package dev.detekt.rules.style

import dev.detekt.api.Config
import dev.detekt.api.DetektVisitor
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.psi.isInternal
import dev.detekt.psi.isOverride
import org.jetbrains.kotlin.config.AnalysisFlags
import org.jetbrains.kotlin.config.ExplicitApiMode
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstructor
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
 * The rule also reports `internal` modifiers that have no effect: on members of private or local classes,
 * and on constructors of `internal` classes.
 *
 * <noncompliant>
 * public interface Foo { // public per default
 *
 *     public fun bar() // public per default
 * }
 *
 * internal class Bar internal constructor() // constructor is already effectively internal
 * </noncompliant>
 *
 * <compliant>
 * interface Foo {
 *
 *     fun bar()
 * }
 *
 * internal class Bar()
 * </compliant>
 */
class RedundantVisibilityModifier(config: Config) :
    Rule(config, "Redundant visibility modifiers detected, which can be safely removed.") {
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
        val flag = languageVersionSettings.getFlag(AnalysisFlags.explicitApiMode)
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
        if (!declaration.isInternal()) return
        val containingClass = declaration.containingClassOrObject ?: return
        if (containingClass.isLocal || containingClass.isPrivate()) {
            report(
                Finding(
                    Entity.from(declaration),
                    "The `internal` modifier on ${declaration.name} is redundant and should be removed."
                )
            )
        } else if (declaration is KtConstructor<*> && declaration.isRedundantInternalConstructorOf(containingClass)) {
            report(
                Finding(
                    Entity.from(declaration),
                    "The `internal` modifier on the constructor of ${containingClass.name} is redundant " +
                        "because the class is already internal."
                )
            )
        }
    }

    /**
     * A constructor of an `internal` class can't be visible outside the module, so `internal` adds nothing.
     * Sealed classes are skipped as their constructors are `protected` by default, and `@PublishedApi`
     * requires an explicit `internal` modifier.
     */
    private fun KtConstructor<*>.isRedundantInternalConstructorOf(klass: KtClassOrObject): Boolean =
        klass.isInternal() &&
            !klass.hasModifier(KtTokens.SEALED_KEYWORD) &&
            annotationEntries.none { it.shortName?.asString() == "PublishedApi" }

    private inner class ClassVisitor : DetektVisitor() {
        override fun visitClass(klass: KtClass) {
            super.visitClass(klass)
            if (klass.isExplicitlyPublic()) {
                report(
                    Finding(
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
                    Finding(
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
                    Finding(
                        Entity.atName(property),
                        message = "${property.name} is explicitly marked as public. " +
                            "Properties are public by default so this modifier is redundant."
                    )
                )
            }
        }
    }
}
