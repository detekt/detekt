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
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty

/**
 *
 * <noncompliant>
 * abstract interface Foo { // abstract keyword not needed
 *
 *     abstract fun x() // abstract keyword not needed
 *     abstract var y: Int // abstract keyword not needed
 * }
 * </noncompliant>
 *
 * <compliant>
 * interface Foo {
 *
 *     fun x()
 *     var y: Int
 * }
 * </compliant>
 *
 * @active since v1.0.0
 * @author schalkms
 * @author Marvin Ramin
 */
class OptionalAbstractKeyword(config: Config = Config.empty) : Rule(config) {

	private val visitor = AbstractDeclarationVisitor(this)

	override val issue: Issue = Issue(javaClass.simpleName, Severity.Style,
			"Unnecessary abstract modifier in interface", Debt.FIVE_MINS)

	override fun visitClass(klass: KtClass) {
		if (klass.isInterface()) {
			handleAbstractKeyword(klass)
			klass.getBody()?.declarations?.forEach { it.accept(visitor) }
		}
		super.visitClass(klass)
	}

	internal fun handleAbstractKeyword(dcl: KtDeclaration) {
		dcl.modifierList?.let {
			val abstractModifier = it.getModifier(KtTokens.ABSTRACT_KEYWORD)
			if (abstractModifier != null) {
				report(CodeSmell(issue, Entity.from(dcl), "The abstract keyword on this declaration " +
						 "is unnecessary."))
			}
		}
	}

	private class AbstractDeclarationVisitor(val rule: OptionalAbstractKeyword) : DetektVisitor() {

		override fun visitProperty(property: KtProperty) {
			rule.handleAbstractKeyword(property)
		}

		override fun visitNamedFunction(function: KtNamedFunction) {
			rule.handleAbstractKeyword(function)
		}
	}
}
