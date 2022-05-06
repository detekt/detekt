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
import org.jetbrains.kotlin.psi.psiUtil.isProtected
import org.jetbrains.kotlin.psi.psiUtil.isPublic

/**
 * This rule will report any KDoc comments that refer to non-public properties of a class.
 * Clients do not need to know the implementation details.
 *
 * <noncompliant>
 * /**
 *  * Comment
 *  * [prop1] - non-public property
 *  * [prop2] - public property
 *  */
 * class Test {
 *     private val prop1 = 0
 *     val prop2 = 0
 * }
 * </noncompliant>
 *
 * <compliant>
 * /**
 *  * Comment
 *  * [prop2] - public property
 *  */
 * class Test {
 *     private val prop1 = 0
 *     val prop2 = 0
 * }
 * </compliant>
 *
 */
class KDocReferencesNonPublicProperty(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(
        javaClass.simpleName,
        Severity.Maintainability,
        "KDoc comments should not refer to non-public properties.",
        Debt.FIVE_MINS
    )

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)

        val enclosingClass = property.getTopmostParentOfType<KtClass>()
        val comment = enclosingClass?.docComment?.text ?: return

        if (property.isNonPublicInherited() && property.isReferencedInherited(comment)) {
            report(property)
        }
    }

    private fun KtProperty.isNonPublicInherited(): Boolean {
        if (!isPublic && !isProtected()) {
            return true
        }
        var classOrObject = containingClassOrObject
        while (classOrObject is KtObjectDeclaration) {
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
        while (classOrObject is KtObjectDeclaration) {
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
                    "is non-public and should not be referenced from KDoc comments."
            )
        )
    }
}
