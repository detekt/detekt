package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/**
 * @author Artur Bosch
 */
class NoDocOverPublicClass(config: Config = Config.EMPTY) : Rule("NoDocOverPublicClass", Severity.Maintainability, config) {

	override fun visitClass(klass: KtClass) {

		if (klass.isPublicNotOverriden()) {
			addFindings(CodeSmell(id, Entity.from(klass)))
		}
		if (klass.notEnum()) { // Stop considering enum entries
			super.visitClass(klass)
		}
	}

	private fun KtClass.notEnum() = !this.isEnum()

	override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
		if (declaration.isCompanionWithoutName())
			return

		if (declaration.isPublicNotOverriden()) {
			addFindings(CodeSmell(id, Entity.from(declaration)))
		}
		super.visitObjectDeclaration(declaration)
	}

	private fun KtObjectDeclaration.isCompanionWithoutName() =
			this.isCompanion() && this.nameAsSafeName.asString() == "Companion"

}