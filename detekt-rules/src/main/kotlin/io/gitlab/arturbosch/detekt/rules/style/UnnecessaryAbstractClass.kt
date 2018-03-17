package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
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

/**
 * This rule inspects `abstract` classes. In case an `abstract class` does not have any concrete members it should be
 * refactored into an interfacse. Abstract classes which do not define any `abstract` members should instead be
 * refactored into concrete classes.
 *
 * <noncompliant>
 * abstract class OnlyAbstractMembersInAbstractClass { // violation: no concrete members
 *
 *     abstract val i: Int
 *     abstract fun f()
 * }
 *
 * abstract class OnlyConcreteMembersInAbstractClass { // violation: no abstract members
 *
 *     val i: Int = 0
 *     fun f() { }
 * }
 * </noncompliant>
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class UnnecessaryAbstractClass(config: Config = Config.empty) : Rule(config) {

	private val noConcreteMember = "An abstract class without a concrete member can be refactored to an interface."
	private val noAbstractMember = "An abstract class without an abstract member can be refactored to a concrete class."

	override val issue =
			Issue("UnnecessaryAbstractClass", Severity.Style,
					"An abstract class is unnecessary and can be refactored. " +
							"An abstract class should have both abstract and concrete properties or functions. " +
							noConcreteMember + " " + noAbstractMember,
					Debt.FIVE_MINS)

	override fun visitClass(klass: KtClass) {
		if (!klass.isInterface() && klass.isAbstract() && klass.superTypeListEntries.isEmpty()) {
			val body = klass.getBody()
			if (body != null) {
				val namedMembers = body.children.filter { it is KtProperty || it is KtNamedFunction }
				val namedClassMembers = NamedClassMembers(klass, namedMembers)
				namedClassMembers.detectAbstractAndConcreteType()
			} else if (!hasNoConstructorParameter(klass)) {
				report(CodeSmell(issue, Entity.from(klass), noAbstractMember))
			}
		}
		super.visitClass(klass)
	}

	private fun hasNoConstructorParameter(klass: KtClass): Boolean {
		val primaryConstructor = klass.primaryConstructor
		return primaryConstructor == null || !primaryConstructor.valueParameters.any()
	}

	private inner class NamedClassMembers(val klass: KtClass, val namedMembers: List<PsiElement>) {

		fun detectAbstractAndConcreteType() {
			val indexOfFirstAbstractMember = indexOfFirstMember(true)
			if (indexOfFirstAbstractMember == -1) {
				report(CodeSmell(issue, Entity.from(klass), noAbstractMember))
			} else if (isAbstractClassWithoutConcreteMembers(indexOfFirstAbstractMember)) {
				report(CodeSmell(issue, Entity.from(klass), noConcreteMember))
			}
		}

		private fun indexOfFirstMember(isAbstract: Boolean, members: List<PsiElement> = this.namedMembers): Int {
			return members.indexOfFirst {
				val namedDeclaration = it as? KtNamedDeclaration
				namedDeclaration != null && namedDeclaration.hasModifier(KtTokens.ABSTRACT_KEYWORD) == isAbstract
			}
		}

		private fun isAbstractClassWithoutConcreteMembers(indexOfFirstAbstractMember: Int) =
				indexOfFirstAbstractMember == 0 && hasNoConcreteMemberLeft() && hasNoConstructorParameter(klass)

		private fun hasNoConcreteMemberLeft() = indexOfFirstMember(false, namedMembers.drop(1)) == -1
	}
}
