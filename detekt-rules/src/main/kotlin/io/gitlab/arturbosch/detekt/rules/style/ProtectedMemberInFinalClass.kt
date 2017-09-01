package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration

class ProtectedMemberInFinalClass(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Warning,
			"Member with protected visibility in final class is private. Consider using private or internal as modifier.",
			Debt.TEN_MINS)

	private val visitor = DeclarationVisitor()

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		val isNotAbstract = !classOrObject.hasModifier(KtTokens.ABSTRACT_KEYWORD)
		val isFinal = !classOrObject.hasModifier(KtTokens.OPEN_KEYWORD)
		val isNotSealed = !classOrObject.hasModifier(KtTokens.SEALED_KEYWORD)
		if (isNotAbstract && isFinal && isNotSealed) {
			visitPrimaryConstructor(classOrObject)
			visitKlassDeclarations(classOrObject)
		} else {
			super.visitClassOrObject(classOrObject)
		}
	}

	private fun visitPrimaryConstructor(classOrObject: KtClassOrObject) {
		classOrObject.primaryConstructor?.accept(visitor)
	}

	private fun visitKlassDeclarations(klass: KtClassOrObject) {
		klass.getBody()?.declarations?.forEach {
			it.accept(visitor)
		}
	}

	internal inner class DeclarationVisitor : DetektVisitor() {

		override fun visitDeclaration(dcl: KtDeclaration) {
			val isProtected = dcl.hasModifier(KtTokens.PROTECTED_KEYWORD)
			val isNotOverridden = !dcl.hasModifier(KtTokens.OVERRIDE_KEYWORD)
			if (isProtected && isNotOverridden) {
				report(CodeSmell(issue, Entity.from(dcl)))
			}
			super.visitDeclaration(dcl)
		}
	}
}
