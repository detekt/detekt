package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.getTopmostParentOfType
import org.jetbrains.kotlin.psi.psiUtil.isPublic

/**
 * This rule will report any KDoc comments that refer to encapsulated properties of a class.
 * Clients do not need to know the implementation details.
 * See [Encapsulation](https://en.wikipedia.org/wiki/Encapsulation_(computer_programming))
 */
class ReferencedEncapsulatedProperty(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "KDoc comments should not refer to encapsulated properties.",
        Debt.FIVE_MINS
    )

    override fun visitProperty(property: KtProperty) {
        val enclosingClass = property.getTopmostParentOfType<KtClass>()
        val comment = enclosingClass?.docComment?.text ?: return

        if (property.isEncapsulatedInherited() && property.isReferencedInherited(comment)) {
            report(property)
        }

        super.visitProperty(property)
    }

    private fun KtProperty.isEncapsulatedInherited(): Boolean {
        if (!isPublic) {
            return true
        }
        var classOrObject = containingClassOrObject
        while (classOrObject != null && classOrObject is KtObjectDeclaration) {
            if (!classOrObject.isPublic) {
                return true
            }
            classOrObject = classOrObject.containingClassOrObject
        }
        return false
    }

    private fun KtProperty.isReferencedInherited(comment: String): Boolean {
        var qualifiedName = nameAsSafeName.asString()
        var classOrObject = containingClassOrObject
        while (classOrObject != null && classOrObject is KtObjectDeclaration) {
            qualifiedName = "${classOrObject.nameAsSafeName.asString()}.$qualifiedName"
            classOrObject = classOrObject.containingClassOrObject
        }
        return comment.contains("[$qualifiedName]")
    }

    private fun report(property: KtNamedDeclaration) {
        report(
            CodeSmell(
                issue,
                Entity.atName(property),
                "The property ${property.nameAsSafeName} " +
                    "is encapsulated and should not be referenced from KDoc comments."
            )
        )
    }
}
