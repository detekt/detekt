package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtVariableDeclaration
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType

/**
 * @author Artur Bosch
 */
class NamingConventionViolation(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"All names in the codebase should be matching the naming convention of the codebase.",
			Debt.FIVE_MINS)

	private val variablePattern = Regex(valueOrDefault(VARIABLE_PATTERN, "^(_)?[a-z$][a-zA-Z$0-9]*$"))
	private val constantPattern = Regex(valueOrDefault(CONSTANT_PATTERN, "^([A-Z_]*|serialVersionUID)$"))
	private val methodPattern = Regex(valueOrDefault(METHOD_PATTERN, "^[a-z$][a-zA-Z$0-9]*$"))
	private val classPattern = Regex(valueOrDefault(CLASS_PATTERN, "^[A-Z$][a-zA-Z$]*$"))
	private val enumEntryPattern = Regex(valueOrDefault(ENUM_PATTERN, "^[A-Z$][a-zA-Z_$]*$"))
	private val packagePattern = Regex(valueOrDefault(PACKAGE_PATTERN, "^[a-z]+(\\.[a-z][a-z0-9]*)*$"))

	override fun visitPackageDirective(directive: KtPackageDirective, data: Void?): Void? {
		val name = directive.qualifiedName
		if (name.isNotEmpty() && !name.matches(packagePattern)) {
			report(CodeSmell(issue, Entity.from(directive)))
		}
		return null
	}

	override fun visitNamedDeclaration(declaration: KtNamedDeclaration) {
		if (declaration.nameAsSafeName.isSpecial) return
		declaration.nameIdentifier?.parent?.javaClass?.let {
			val name = declaration.nameIdentifier?.text ?: SpecialNames.NO_NAME_PROVIDED.asString()
			when (declaration) {
				is KtVariableDeclaration -> handleVariableNamings(declaration, name)
				is KtNamedFunction -> if (!name.matches(methodPattern)) add(declaration)
				is KtEnumEntry -> if (!name.matches(enumEntryPattern)) add(declaration)
				is KtClassOrObject -> if (!name.matches(classPattern)) add(declaration)
			}
		}
		super.visitNamedDeclaration(declaration)
	}

	private fun handleVariableNamings(declaration: KtVariableDeclaration, name: String) {
		if (declaration.hasConstModifier()) {
			if (!name.matches(constantPattern)) {
				add(declaration)
			}
		} else if (declaration.withinObjectDeclaration() || declaration.isTopLevel()) {
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
		report(CodeSmell(issue, Entity.from(declaration)))
	}

	private fun KtVariableDeclaration.isTopLevel(): Boolean = this is KtProperty && this.isTopLevel

	companion object {
		const val RULE_SUB_CONFIG = "NamingConventionViolation"
		const val VARIABLE_PATTERN = "variablePattern"
		const val CONSTANT_PATTERN = "constantPattern"
		const val METHOD_PATTERN = "methodPattern"
		const val CLASS_PATTERN = "classPattern"
		const val ENUM_PATTERN = "enumEntryPattern"
		const val PACKAGE_PATTERN = "packagePattern"
	}

}
