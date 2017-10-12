package io.gitlab.arturbosch.detekt.rules.style.naming

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.MultiRule
import io.gitlab.arturbosch.detekt.api.Rule
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
	private val forbiddenClassNameRule = ForbiddenClassName(config)

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
			functionMinNameLengthRule,
			forbiddenClassNameRule
	)

	override fun visitPackageDirective(directive: KtPackageDirective) {
		super.visitPackageDirective(directive)
		packageNamingRule.runIfActive { visitPackageDirective(directive) }
	}

	override fun visitNamedDeclaration(declaration: KtNamedDeclaration) {
		if (declaration.nameAsSafeName.isSpecial) {
			return
		}
		declaration.nameIdentifier?.parent?.javaClass?.let {
			when (declaration) {
				is KtProperty -> handleVariable(declaration)
				is KtNamedFunction -> handleFunction(declaration)
				is KtEnumEntry -> enumEntryNamingRule.runIfActive { visitEnumEntry(declaration) }
				is KtClassOrObject -> handleClassOrObject(declaration)
			}
		}
		super.visitNamedDeclaration(declaration)
	}

	private fun handleClassOrObject(declaration: KtClassOrObject) {
		classOrObjectNamingRule.runIfActive { visitClassOrObject(declaration) }
		forbiddenClassNameRule.runIfActive { visitClassOrObject(declaration) }
	}

	private fun handleFunction(declaration: KtNamedFunction) {
		functionNamingRule.runIfActive { visitNamedFunction(declaration) }
		functionMaxNameLengthRule.runIfActive { visitNamedFunction(declaration) }
		functionMinNameLengthRule.runIfActive { visitNamedFunction(declaration) }
	}

	private fun handleVariable(declaration: KtProperty) {
		variableMaxNameLengthRule.runIfActive { visitProperty(declaration) }
		variableMinNameLengthRule.runIfActive { visitProperty(declaration) }

		if (declaration.hasConstModifier()) {
			constantNamingRule.runIfActive { visitProperty(declaration) }
		} else if (declaration.withinObjectDeclaration() || declaration.isTopLevel) {
			if (variableNamingRule.doesntMatchPattern(declaration)
					&& constantNamingRule.doesntMatchPattern(declaration)) {
				variableNamingRule.runIfActive { visitProperty(declaration) }
			}
		} else {
			variableNamingRule.runIfActive { visitProperty(declaration) }
		}
	}

	private fun KtVariableDeclaration.hasConstModifier(): Boolean {
		val modifierList = this.modifierList
		return modifierList != null && modifierList.hasModifier(KtTokens.CONST_KEYWORD)
	}

	private fun KtVariableDeclaration.withinObjectDeclaration(): Boolean {
		return this.getNonStrictParentOfType(KtObjectDeclaration::class.java) != null
	}
}

internal fun KtNamedDeclaration.identifierName() = nameIdentifier?.text ?: SpecialNames.NO_NAME_PROVIDED.asString()
