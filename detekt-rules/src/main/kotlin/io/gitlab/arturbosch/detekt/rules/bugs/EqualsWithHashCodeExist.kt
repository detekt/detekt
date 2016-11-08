package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * @author Artur Bosch
 */
class EqualsWithHashCodeExist(config: Config = Config.empty) : Rule("EqualsWithHashCodeExist", Severity.Defect, config) {

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		val isDataClass = classOrObject.modifierList?.hasModifier(KtTokens.DATA_KEYWORD) ?: false
		if (isDataClass) return

		val visitor = EqualsAndHashCodeVisitor()
		visitor.visitClassOrObject(classOrObject)
		if (visitor.violation()) addFindings(CodeSmell(id, Entity.from(classOrObject)))
	}

	private class EqualsAndHashCodeVisitor : DetektVisitor() {
		private var equals = false
		private var hashCode = false

		override fun visitNamedFunction(function: KtNamedFunction) {
			function.name?.let {
				if (it == "equals") equals = true
				if (it == "hashCode") hashCode = true
			}
		}

		internal fun violation() = equals && !hashCode || !equals && hashCode
	}
}