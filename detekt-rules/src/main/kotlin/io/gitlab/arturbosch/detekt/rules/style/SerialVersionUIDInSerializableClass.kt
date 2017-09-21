package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty

class SerialVersionUIDInSerializableClass(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Warning,
			"A class which implements the Serializable interface does not define a correct serialVersionUID field. " +
					"The serialVersionUID field should be a constant long value inside a companion object.",
			Debt.FIVE_MINS)

	private val versionUID = "serialVersionUID"

	override fun visitClass(klass: KtClass) {
		if (!klass.isInterface() && isImplementingSerializable(klass)) {
			val companionObject = klass.companionObjects.singleOrNull { it.isCompanion() }
			if (companionObject == null || !hasCorrectSerialVersionUUID(companionObject)) {
				report(CodeSmell(issue, Entity.from(klass)))
			}
		}
		super.visitClass(klass)
	}

	override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
		if (!declaration.isCompanion()
				&& isImplementingSerializable(declaration)
				&& !hasCorrectSerialVersionUUID(declaration)) {
			report(CodeSmell(issue, Entity.from(declaration)))
		}
		super.visitObjectDeclaration(declaration)
	}

	private fun isImplementingSerializable(classOrObject: KtClassOrObject) =
			classOrObject.superTypeListEntries.any { it.text == "Serializable" }

	private fun hasCorrectSerialVersionUUID(declaration: KtObjectDeclaration): Boolean {
		val property = declaration.getBody()?.properties?.firstOrNull { it.name == versionUID }
		return property != null && property.hasModifier(KtTokens.CONST_KEYWORD) && isLongProperty(property)
	}

	private fun isLongProperty(property: KtProperty) = hasLongType(property) || hasLongAssignment(property)

	private fun hasLongType(property: KtProperty) = property.typeReference?.text == "Long"

	private fun hasLongAssignment(property: KtProperty): Boolean {
		val assignmentText = property.children.singleOrNull { it is KtConstantExpression }?.text
		return assignmentText != null && assignmentText.last() == 'L'
				&& assignmentText.substring(0, assignmentText.length-1).toLongOrNull() != null
	}
}
