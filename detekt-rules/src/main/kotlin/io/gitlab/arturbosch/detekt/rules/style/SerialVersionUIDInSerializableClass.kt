package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.companionObject
import io.gitlab.arturbosch.detekt.rules.isConstant
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtProperty

/**
 * Classes which implement the `Serializable` interface should also correctly declare a `serialVersionUID`.
 * This rule verifies that a `serialVersionUID` was correctly defined.
 *
 * <noncompliant>
 * class IncorrectSerializable : Serializable {
 *
 *     companion object {
 *         val serialVersionUID = 1 // wrong declaration for UID
 *     }
 * }
 * </noncompliant>
 *
 * <compliant>
 * class CorrectSerializable : Serializable {
 *
 *     companion object {
 *         const val serialVersionUID = 1L
 *     }
 * }
 * </compliant>
 */
class SerialVersionUIDInSerializableClass(config: Config = Config.empty) : Rule(config) {

    override val issue = Issue(javaClass.simpleName, Severity.Warning,
            "A class which implements the Serializable interface does not define a correct serialVersionUID field. " +
                    "The serialVersionUID field should be a constant long value inside a companion object.",
            Debt.FIVE_MINS)

    private val versionUID = "serialVersionUID"

    override fun visitClass(klass: KtClass) {
        if (!klass.isInterface() && isImplementingSerializable(klass)) {
            val companionObject = klass.companionObject()
            if (companionObject == null || !hasCorrectSerialVersionUUID(companionObject)) {
                report(CodeSmell(issue, Entity.from(klass), "The class ${klass.nameAsSafeName} implements" +
                        " the Serializable interface and should thus define a serialVersionUID."))
            }
        }
        super.visitClass(klass)
    }

    override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
        if (!declaration.isCompanion() &&
                isImplementingSerializable(declaration) &&
                !hasCorrectSerialVersionUUID(declaration)) {
            report(CodeSmell(issue, Entity.from(declaration), "The object ${declaration.nameAsSafeName} " +
                    "implements the Serializable interface and should thus define a serialVersionUID."))
        }
        super.visitObjectDeclaration(declaration)
    }

    private fun isImplementingSerializable(classOrObject: KtClassOrObject) =
            classOrObject.superTypeListEntries.any { it.text == "Serializable" }

    private fun hasCorrectSerialVersionUUID(declaration: KtObjectDeclaration): Boolean {
        val property = declaration.body?.properties?.firstOrNull { it.name == versionUID }
        return property != null && property.isConstant() && isLongProperty(property)
    }

    private fun isLongProperty(property: KtProperty) = hasLongType(property) || hasLongAssignment(property)

    private fun hasLongType(property: KtProperty) = property.typeReference?.text == "Long"

    private fun hasLongAssignment(property: KtProperty): Boolean {
        val assignmentText = property.children
                .singleOrNull { it is KtConstantExpression || it is KtPrefixExpression }?.text
        return assignmentText != null && assignmentText.last() == 'L' &&
                assignmentText.substring(0, assignmentText.length - 1).toLongOrNull() != null
    }
}
