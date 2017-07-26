package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration

class AbstractInInterface(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue(javaClass.simpleName, Severity.Style,
			"Unnecessary abstract modifier in interface", Debt.FIVE_MINS)

	override fun visitClass(klass: KtClass) {
		if (klass.isInterface()) {
			handleAbstractKeyword(klass)
			klass.getBody()?.declarations?.forEach { visitInterfaceDeclaration(it) }
		}
		super.visitClass(klass)
	}

	private fun handleAbstractKeyword(dcl: KtDeclaration) {
		dcl.modifierList?.let {
			val abstractModifier = it.getModifier(KtTokens.ABSTRACT_KEYWORD)
			if (abstractModifier != null) {
				report(CodeSmell(issue, Entity.from(dcl)))
			}
		}
	}

	private fun visitInterfaceDeclaration(dcl: KtDeclaration) {
		val klass = dcl as? KtClass
		if (klass != null && !klass.isInterface()) {
			return
		}
		handleAbstractKeyword(dcl)
	}
}
