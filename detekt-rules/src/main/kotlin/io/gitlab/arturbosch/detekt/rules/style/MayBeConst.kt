package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isConstant
import io.gitlab.arturbosch.detekt.rules.isOverridden
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

/**
 * This rule identifies and reports properties (`val`) that may be `const val` instead.
 * Using `const val` can lead to better performance of the resulting bytecode as well as better interoperability with
 * Java.
 *
 * <noncompliant>
 * val myConstant = "abc"
 * </noncompliant>
 *
 * <compliant>
 * const val MY_CONSTANT = "abc"
 * </compliant>
 *
 * @author Marvin Ramin
 */
class MayBeConst(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Reports vals that can be const val instead.",
			Debt.FIVE_MINS)

	override fun visitProperty(property: KtProperty) {
		super.visitProperty(property)

		if (property.canBeConst()) {
			report(CodeSmell(issue, Entity.from(property), "${property.nameAsSafeName} can be a `const val`."))
		}
	}

	private fun KtProperty.canBeConst(): Boolean {
		if (isLocal
				|| isVar
				|| getter != null
				|| isConstant()
				|| isOverridden()) {
			return false
		}

		if (!isTopLevel && containingClassOrObject !is KtObjectDeclaration) return false

		val isJvmField = annotationEntries.any { it.text == "@JvmField" }
		if (annotationEntries.isNotEmpty() && !isJvmField) return false

		val initializer = initializer ?: return false

		return initializer.isConstantExpression()
	}

	private fun KtExpression.isConstantExpression(): Boolean {
		return this is KtStringTemplateExpression
				|| node.elementType == KtNodeTypes.BOOLEAN_CONSTANT
				|| node.elementType == KtNodeTypes.INTEGER_CONSTANT
				|| node.elementType == KtNodeTypes.CHARACTER_CONSTANT
				|| node.elementType == KtNodeTypes.FLOAT_CONSTANT
	}

}
