package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * @author Artur Bosch
 */
class NoDocOverPublicClass(config: Config = Config.EMPTY) : Rule("NoDocOverPublicClass", Severity.Maintainability, config) {

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		if (classOrObject.isPublicClass()) {
			addFindings(CodeSmell(id, Location.from(classOrObject, classOrObject.getBody())))
		}
		super.visitClassOrObject(classOrObject)
	}

	private fun KtClassOrObject.isPublicClass() = this.hasModifier(KtTokens.PUBLIC_KEYWORD)
			|| (this.hasModifier(KtTokens.PRIVATE_KEYWORD) || this.hasModifier(KtTokens.PROTECTED_KEYWORD)
			|| this.hasModifier(KtTokens.INTERNAL_KEYWORD)).not()
}