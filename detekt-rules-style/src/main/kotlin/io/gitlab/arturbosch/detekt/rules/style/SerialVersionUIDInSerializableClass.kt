package io.gitlab.arturbosch.detekt.rules.style

import dev.detekt.api.ActiveByDefault
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.psi.companionObject
import dev.detekt.psi.isConstant
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtPrefixExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.isPrivate

/**
 * Classes which implement the `Serializable` interface should also correctly declare a `serialVersionUID`.
 * This rule verifies that a `serialVersionUID` was correctly defined and declared as `private`.
 *
 * [More about `SerialVersionUID`](https://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html)
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
 *         private const val serialVersionUID = 1L
 *     }
 * }
 * </compliant>
 */
@ActiveByDefault(since = "1.16.0")
class SerialVersionUIDInSerializableClass(config: Config) : Rule(
    config,
    "A class which implements the Serializable interface does not define a correct serialVersionUID field. " +
        "The serialVersionUID field should be a private constant long value inside a companion object."
) {

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        if (!klass.isInterface() && isImplementingSerializable(klass)) {
            val companionObject = klass.companionObject()
            if (companionObject == null) {
                report(
                    Finding(
                        Entity.atName(klass),
                        klass.getIssueMessage("class")
                    )
                )
            } else {
                val finding = searchSerialVersionUIDFinding(companionObject, klass) ?: return
                reportFinding(finding)
            }
        }
    }

    override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
        super.visitObjectDeclaration(declaration)
        if (!declaration.isCompanion() && isImplementingSerializable(declaration)) {
            val finding = searchSerialVersionUIDFinding(declaration) ?: return
            reportFinding(finding)
        }
    }

    private fun reportFinding(finding: SerialVersionUIDFindings) {
        report(
            Finding(
                Entity.atName(finding.violatingElement),
                finding.issueMsg
            )
        )
    }

    private fun isImplementingSerializable(classOrObject: KtClassOrObject) =
        classOrObject.superTypeListEntries.any { it.text == "Serializable" }

    private fun searchSerialVersionUIDFinding(
        declaration: KtObjectDeclaration,
        parentDeclaration: KtNamedDeclaration = declaration,
    ): SerialVersionUIDFindings? {
        val property = declaration.body?.properties?.firstOrNull { it.name == "serialVersionUID" }
            ?: return SerialVersionUIDFindings(
                parentDeclaration,
                parentDeclaration.getIssueMessage(
                    if (parentDeclaration is KtClass) "class" else "object"
                )
            )
        val isPropertyPrivate = declaration.isPrivate() || property.isPrivate()
        return if (property.isConstant() && isLongProperty(property) && isPropertyPrivate) {
            null
        } else {
            SerialVersionUIDFindings(
                property,
                "The property `serialVersionUID` signature is not correct. `serialVersionUID` should be " +
                    "`private` and `constant` and its type should be `Long`"
            )
        }
    }

    private fun KtNamedDeclaration.getIssueMessage(typeOfDeclaration: String) =
        "The $typeOfDeclaration ${this.nameAsSafeName} " +
            "implements the `Serializable` interface and should thus define a `serialVersionUID`."

    private fun isLongProperty(property: KtProperty) = hasLongType(property) || hasLongAssignment(property)

    private fun hasLongType(property: KtProperty) = property.typeReference?.text == "Long"

    private fun hasLongAssignment(property: KtProperty): Boolean {
        val assignmentText = property.children
            .singleOrNull { it is KtConstantExpression || it is KtPrefixExpression }
            ?.text
        return assignmentText != null &&
            assignmentText.last() == 'L' &&
            assignmentText.substring(0, assignmentText.length - 1).toLongOrNull() != null
    }

    private data class SerialVersionUIDFindings(
        val violatingElement: KtNamedDeclaration,
        val issueMsg: String,
    )
}
