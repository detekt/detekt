package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isInternal
import io.gitlab.arturbosch.detekt.rules.isOverride
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
class RedundantVisibilityModifierRule(config: Config = Config.empty) : Rule(config) {

    override val issue: Issue = Issue(
        "RedundantVisibilityModifierRule",
        Severity.Style,
        "Checks for redundant visibility modifiers.",
        Debt.FIVE_MINS
    )

    private val classVisitor = ClassVisitor()
    private val childrenVisitor = ChildrenVisitor()

    private fun KtModifierListOwner.isExplicitlyPublicNotOverridden() = isExplicitlyPublic() && !isOverride()

    private fun KtModifierListOwner.isExplicitlyPublic() = this.hasModifier(KtTokens.PUBLIC_KEYWORD)

    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        file.declarations.forEach {
            it.accept(classVisitor)
            it.acceptChildren(childrenVisitor)
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
                    issue,
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
                        issue,
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
                    CodeSmell(issue,
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
                        issue,
                        Entity.atName(property),
                        message = "${property.name} is explicitly marked as public. " +
                            "Properties are public by default so this modifier is redundant."
                    )
                )
            }
        }
    }
}
