package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isOverridden
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.psiUtil.isProtected

/**
 * Kotlin classes are `final` by default. Thus classes which are not marked as `open` should not contain any `protected`
 * members. Consider using `private` or `internal` modifiers instead.
 *
 * <noncompliant>
 * class ProtectedMemberInFinalClass {
 *     protected var i = 0
 * }
 * </noncompliant>
 *
 * <compliant>
 * class ProtectedMemberInFinalClass {
 *     private var i = 0
 * }
 * </compliant>
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class ProtectedMemberInFinalClass(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Warning,
			"Member with protected visibility in final class is private. Consider using private or internal as modifier.",
			Debt.FIVE_MINS)

	private val visitor = DeclarationVisitor()

	/**
	 * Only classes and companion objects can contain protected members.
	 */
	override fun visitClass(klass: KtClass) {
		if (hasModifiers(klass)) {
			klass.primaryConstructor?.accept(visitor)
			klass.getBody()?.declarations?.forEach { it.accept(visitor) }
			klass.companionObjects.forEach { it.accept(visitor) }
		}
		super.visitClass(klass)
	}

	private fun hasModifiers(klass: KtClass): Boolean {
		val isNotAbstract = !klass.hasModifier(KtTokens.ABSTRACT_KEYWORD)
		val isFinal = !klass.hasModifier(KtTokens.OPEN_KEYWORD)
		val isNotSealed = !klass.hasModifier(KtTokens.SEALED_KEYWORD)
		return isNotAbstract && isFinal && isNotSealed
	}

	internal inner class DeclarationVisitor : DetektVisitor() {

		override fun visitDeclaration(dcl: KtDeclaration) {
			if (dcl.isProtected() && !dcl.isOverridden()) {
				report(CodeSmell(issue, Entity.from(dcl), issue.description))
			}
		}
	}
}
