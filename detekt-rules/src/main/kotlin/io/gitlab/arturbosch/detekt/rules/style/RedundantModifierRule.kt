package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.psi.KtNamedFunction

class RedundantModifierRule(config: Config = Config.empty) : Rule(config) {
	override val issue: Issue = Issue("RedundantModifierRule", Severity.Style, "TODO")

	private val classVisitor = ClassVisitor()
	private val functionVisitor = FunctionVisitor()

	private fun KtModifierListOwner.isPublicNotOverridden() = isPublic() && !this.hasModifier(KtTokens.OVERRIDE_KEYWORD)
	private fun KtModifierListOwner.isPublic() = this.hasModifier(KtTokens.PUBLIC_KEYWORD)

	override fun visitKtFile(file: KtFile) {
		super.visitKtFile(file)
		file.declarations.forEach {
			it.accept(classVisitor)
			it.acceptChildren(functionVisitor)
		}
	}

	private inner class ClassVisitor : DetektVisitor() {
		override fun visitClass(klass: KtClass) {
			super.visitClass(klass)
			if (klass.isPublic()) {
				report(CodeSmell(issue, Entity.from(klass)))
			}
		}
	}

	private inner class FunctionVisitor : DetektVisitor(){
		override fun visitNamedFunction(function: KtNamedFunction) {
			super.visitNamedFunction(function)
			if (function.isPublicNotOverridden()) {
				report(CodeSmell(issue, Entity.from(function)))
			}
		}
	}
}
