package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.isAbstract

class UnnecessaryAbstractClass(config: Config = Config.empty) : Rule(config) {

	override val issue =
			Issue("UnnecessaryAbstractClass", Severity.Style,
					"An abstract class is unnecessary and can be refactored. " +
							"An abstract class should have both abstract and concrete properties or functions. " +
							"An abstract class without a concrete member can be refactored to an interface. " +
							"An abstract class without an abstract member can be refactored to a concrete class.")

	override fun visitClass(klass: KtClass) {
		if (!klass.isInterface() && klass.isAbstract()) {
			val namedMembers = klass.getBody()?.children
					?.filter { it is KtProperty || it is KtNamedFunction }
			if (namedMembers != null) {
				val namedClassMembers = NamedClassMembers(klass, namedMembers)
				namedClassMembers.detectAbstractAndConcreteType()
			}
		}
		super.visitClass(klass)
	}

	private inner class NamedClassMembers(val klass: KtClass, val namedMembers: List<PsiElement>) {

		fun detectAbstractAndConcreteType() {
			val indexOfFirstAbstractMember = indexOfFirstMember(true)
			if (indexOfFirstAbstractMember == -1) {
				report(CodeSmell(issue,
						Entity.from(klass),
						"An abstract class without an abstract member can be refactored to a concrete class."))
			}
			if (indexOfFirstAbstractMember == 0 && hasNoConcreteMemberLeft() && hasNoConstructorParameter(klass)) {
				report(CodeSmell(issue,
						Entity.from(klass),
						"An abstract class without a concrete member can be refactored to an interface."))
			}
		}

		private fun indexOfFirstMember(isAbstract: Boolean, members: List<PsiElement> = this.namedMembers): Int {
			return members.indexOfFirst {
				val namedDeclaration = it as? KtNamedDeclaration
				namedDeclaration != null && namedDeclaration.hasModifier(KtTokens.ABSTRACT_KEYWORD) == isAbstract
			}
		}

		private fun hasNoConcreteMemberLeft() = indexOfFirstMember(false, namedMembers.drop(1)) == -1

		private fun hasNoConstructorParameter(klass: KtClass): Boolean {
			val primaryConstructor = klass.primaryConstructor
			return primaryConstructor == null || !primaryConstructor.valueParameters.any()
		}
	}
}
