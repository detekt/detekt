package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtVariableDeclaration
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

/**
 * @author Artur Bosch
 */
class NamingConventionViolation(config: Config = Config.empty) : Rule("NamingConventionViolation", Severity.Style, config) {

	private val variablePattern = Regex("^(_)?[a-z$][a-zA-Z$0-9]*$")
	private val constantPattern = Regex("^([A-Z_]*|serialVersionUID)$")
	private val methodPattern = Regex("^[a-z$][a-zA-Z$0-9]*$")
	private val classPattern = Regex("^[A-Z$][a-zA-Z$]*$")

	override fun visitNamedDeclaration(declaration: KtNamedDeclaration) {
		if (declaration.nameAsSafeName.isSpecial) return
		declaration.nameIdentifier?.parent?.javaClass?.let {
			val name = declaration.nameAsSafeName.asString()
			if (declaration is KtVariableDeclaration) {
				handleVariableNamings(declaration, name)
			}
			if (declaration is KtNamedFunction && !name.matches(methodPattern)) {
				add(declaration)
			}
			if (declaration is KtClassOrObject && !name.matches(classPattern)) {
				add(declaration)
			}
		}
		super.visitNamedDeclaration(declaration)
	}

	private fun handleVariableNamings(declaration: KtVariableDeclaration, name: String) {
		if (declaration.hasConstModifier()) {
			if (!name.matches(constantPattern)) {
				add(declaration)
			}
		} else if (declaration.withinObjectDeclaration()) {
			if (!name.matches(constantPattern) && !name.matches(variablePattern)) {
				add(declaration)
			}
		} else if (!name.matches(variablePattern)) {
			add(declaration)
		}
	}

	private fun KtVariableDeclaration.hasConstModifier(): Boolean {
		val modifierList = this.modifierList
		return modifierList != null && modifierList.hasModifier(KtTokens.CONST_KEYWORD)
	}

	private fun KtVariableDeclaration.withinObjectDeclaration(): Boolean {
		return this.getNonStrictParentOfType(KtObjectDeclaration::class.java) != null
	}

	private fun add(declaration: KtNamedDeclaration) {
		addFindings(CodeSmell(id, Entity.from(declaration)))
	}

}
