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
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

class NestedClassesVisibility(config: Config = Config.empty) : Rule(config) {
	override val issue: Issue = Issue("NestedClassesVisibility", severity = Severity.Security, description = "Nested types are often used for implementing private functionality. Therefore, they shouldn't be externally visible.")

	private val visitor = VisibilityVisitor(this)

	override fun visitClass(klass: KtClass) {
		super.visitClass(klass)
		if (isInternal(klass)) {
			klass.declarations.forEach {
				it.accept(visitor)
			}
		}
	}

	private fun isInternal(klass: KtClass): Boolean {
		return klass.visibilityModifierType() == KtTokens.INTERNAL_KEYWORD
	}

	private fun isPrivate(klass: KtClass): Boolean {
		return klass.visibilityModifierType() == KtTokens.PRIVATE_KEYWORD
	}

	private fun handleClass(klass: KtClass) {
		report(CodeSmell(issue, Entity.from(klass)))
	}

	private class VisibilityVisitor(val rule: NestedClassesVisibility) : DetektVisitor() {
		override fun visitClass(klass: KtClass) {
			if (!rule.isInternal(klass) && !rule.isPrivate(klass)) {
				rule.handleClass(klass)
			}
		}
	}
}
