package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.*
import io.gitlab.arturbosch.detekt.rules.isPublicNotOverriden
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/**
 * @author Artur Bosch
 */
class NoDocOverPublicClass(config: Config = Config.empty) : Rule("NoDocOverPublicClass", config) {

	override fun visitClass(context: Context, klass: KtClass) {

		if (klass.isPublicNotOverriden()) {
			context.report(CodeSmell(ISSUE, Entity.Companion.from(klass)))
		}
		if (klass.notEnum()) { // Stop considering enum entries
			super.visitClass(context, klass)
		}
	}

	private fun KtClass.notEnum() = !this.isEnum()

	override fun visitObjectDeclaration(context: Context, declaration: KtObjectDeclaration) {
		if (declaration.isCompanionWithoutName())
			return

		if (declaration.isPublicNotOverriden()) {
			context.report(CodeSmell(ISSUE, Entity.Companion.from(declaration)))
		}
		super.visitObjectDeclaration(context, declaration)
	}

	private fun KtObjectDeclaration.isCompanionWithoutName() =
			this.isCompanion() && this.nameAsSafeName.asString() == "Companion"

	companion object {
		val ISSUE = Issue("NoDocOverPublicClass", Issue.Severity.Maintainability)
	}
}