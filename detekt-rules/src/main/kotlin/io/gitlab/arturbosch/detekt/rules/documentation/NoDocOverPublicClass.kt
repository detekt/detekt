package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isPublicNotOverriden
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/**
 * @author Artur Bosch
 */
class NoDocOverPublicClass(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("NoDocOverPublicClass", Severity.Maintainability, "")

	override fun visitClass(klass: KtClass) {

		if (klass.isPublicNotOverriden()) {
			report(CodeSmell(issue, Entity.Companion.from(klass)))
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
			report(CodeSmell(issue, Entity.Companion.from(declaration)))
		}
		super.visitObjectDeclaration(declaration)
	}

	private fun KtObjectDeclaration.isCompanionWithoutName() =
			this.isCompanion() && this.nameAsSafeName.asString() == "Companion"

}