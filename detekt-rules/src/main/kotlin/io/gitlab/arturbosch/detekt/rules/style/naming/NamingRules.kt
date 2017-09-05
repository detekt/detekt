package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.rules.reportFindings
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtFile
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
class NamingRules(config: Config = Config.empty) : MultiRule() {

	private val variableNamingRule = VariableNaming(config)
	private val variableMinNameLengthRule = VariableMinLength(config)
	private val variableMaxNameLengthRule = VariableMaxLength(config)
	private val constantNamingRule = ConstantNaming(config)
	private val packageNamingRule = PackageNaming(config)
	private val classOrObjectNamingRule = ClassNaming(config)
	private val enumEntryNamingRule = EnumNaming(config)
	private val functionNamingRule = FunctionNaming(config)
	private val functionMaxNameLengthRule = FunctionMaxLength(config)
	private val functionMinNameLengthRule = FunctionMinLength(config)

	override val rules: List<Rule> = listOf(
			variableNamingRule,
			variableMinNameLengthRule,
			variableMaxNameLengthRule,
			constantNamingRule,
			packageNamingRule,
			classOrObjectNamingRule,
			enumEntryNamingRule,
			functionNamingRule,
			functionMaxNameLengthRule,
			functionMinNameLengthRule
	)

	override fun postVisit(root: KtFile) {
		report(rules.flatMap { it.findings })
	}

	override fun visitPackageDirective(directive: KtPackageDirective) {
		super.visitPackageDirective(directive)
		directive.reportFindings(packageNamingRule)
	}

	override fun visitNamedDeclaration(declaration: KtNamedDeclaration) {
		if (declaration.nameAsSafeName.isSpecial) {
			return
		}
		declaration.nameIdentifier?.parent?.javaClass?.let {
			when (declaration) {
				is KtVariableDeclaration -> handleVariable(declaration)
				is KtNamedFunction -> handleFunction(declaration)
				is KtEnumEntry -> enumEntryNamingRule.apply(declaration)
				is KtClassOrObject -> classOrObjectNamingRule.apply(declaration)
			}
		}
		super.visitNamedDeclaration(declaration)
	}

	private fun handleFunction(declaration: KtNamedFunction) {
		declaration.reportFindings(functionNamingRule)
		declaration.reportFindings(functionMaxNameLengthRule)
		declaration.reportFindings(functionMinNameLengthRule)
	}

	private fun handleVariable(declaration: KtVariableDeclaration) {
		declaration.reportFindings(variableMaxNameLengthRule)
		declaration.reportFindings(variableMinNameLengthRule)

		if (declaration.hasConstModifier()) {
			declaration.reportFindings(constantNamingRule)
		} else if (declaration.withinObjectDeclaration() || declaration.isTopLevel()) {
			if (variableNamingRule.doesntMatchPattern(declaration)
					&& constantNamingRule.doesntMatchPattern(declaration)) {
				declaration.reportFindings(variableNamingRule)
			}
		} else {
			declaration.reportFindings(variableNamingRule)
		}
	}

	private fun KtVariableDeclaration.hasConstModifier(): Boolean {
		val modifierList = this.modifierList
		return modifierList != null && modifierList.hasModifier(KtTokens.CONST_KEYWORD)
	}

	private fun KtVariableDeclaration.withinObjectDeclaration(): Boolean {
		return this.getNonStrictParentOfType(KtObjectDeclaration::class.java) != null
	}

	private fun KtVariableDeclaration.isTopLevel(): Boolean = this is KtProperty && this.isTopLevel
}

internal fun KtNamedDeclaration.identifierName() = nameIdentifier?.text ?: SpecialNames.NO_NAME_PROVIDED.asString()
