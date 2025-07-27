package io.gitlab.arturbosch.detekt.rules.documentation

import dev.detekt.api.Config
import dev.detekt.api.DetektVisitor
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
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
class KDocReferencesNonPublicProperty(config: Config) : Rule(
    config,
    "KDoc comments should not refer to non-public properties."
) {

    private val publicPropertiesByClass = mutableMapOf<KtClass, MutableSet<KtNamedDeclaration>>()
    private val privatePropertiesByClass = mutableMapOf<KtClass, MutableSet<KtNamedDeclaration>>()

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)

        val comment = klass.docComment?.text ?: return

        klass.primaryConstructor?.accept(
            object : DetektVisitor() {
                override fun visitParameter(parameter: KtParameter) {
                    super.visitParameter(parameter)
                    klass.registerProperty(parameter)
                }
            }
        )

        val privateProperties = privatePropertiesByClass.remove(klass)
        val publicPropertyNames = publicPropertiesByClass.remove(klass)
            ?.mapTo(mutableSetOf()) { it.qualifiedName() }
            .orEmpty()

        for (privateProperty in privateProperties.orEmpty()) {
            val qualifiedName = privateProperty.qualifiedName()
            val matchesPublicProperty = publicPropertyNames.contains(qualifiedName)
            if (!matchesPublicProperty && comment.contains("[$qualifiedName]")) {
                report(privateProperty)
            }
        }
    }

    override fun visitNamedDeclaration(declaration: KtNamedDeclaration) {
        super.visitNamedDeclaration(declaration)
        declaration.getTopmostParentOfType<KtClass>()?.registerProperty(declaration)
    }

    private fun KtClass.registerProperty(declaration: KtNamedDeclaration) {
        if (declaration.isNonPublicInherited()) {
            // only consider property declarations (not constructor parameters) as private properties which cannot be
            // referenced in kdoc
            if (declaration is KtProperty) {
                privatePropertiesByClass.getOrPut(this) { mutableSetOf() }.add(declaration)
            }
        } else {
            publicPropertiesByClass.getOrPut(this) { mutableSetOf() }.add(declaration)
        }
    }

    private fun KtDeclaration.isNonPublicInherited(): Boolean {
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

    private fun KtNamedDeclaration.qualifiedName(): String {
        var qualifiedName = nameAsSafeName.asString()
        var classOrObject = containingClassOrObject
        while (classOrObject is KtObjectDeclaration) {
            qualifiedName = "${classOrObject.nameAsSafeName.asString()}.$qualifiedName"
            classOrObject = classOrObject.containingClassOrObject
        }
        return qualifiedName
    }

    private fun report(property: KtNamedDeclaration) {
        report(
            Finding(
                Entity.atName(property),
                "The property ${property.nameAsSafeName} " +
                    "is non-public and should not be referenced from KDoc comments."
            )
        )
    }
}
