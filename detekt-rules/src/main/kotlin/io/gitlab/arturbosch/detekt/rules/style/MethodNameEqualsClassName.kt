package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration

class MethodNameEqualsClassName(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"A method is named after the class. This might result in confusion. " +
					"Either rename the method or change it to a constructor.",
			Debt.FIVE_MINS)

	private val objectIssue = issue.copy(
			description = "A method is named after the class object. " +
					"This might result in confusion. Please rename the method.")

	override fun visitClass(klass: KtClass) {
		if (!klass.isInterface()) {
			checkClass(klass)
		}
		super.visitClass(klass)
	}

	private fun checkClass(klass: KtClass) {
		if (checkClassOrObjectFunctions(klass, klass.name) || checkCompanionObjectFunctions(klass)) {
			report(CodeSmell(issue, Entity.from(klass)))
		}
	}

	override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
		if (!declaration.isCompanion() && checkClassOrObjectFunctions(declaration, declaration.name)) {
			report(CodeSmell(objectIssue, Entity.from(declaration)))
		}
		super.visitObjectDeclaration(declaration)
	}

	private fun checkClassOrObjectFunctions(klassOrObject: KtClassOrObject, name: String?): Boolean {
		return klassOrObject.getBody()?.children
				?.filterIsInstance<KtNamedFunction>()
				?.any { it.name == name } == true
	}

	private fun checkCompanionObjectFunctions(klass: KtClass): Boolean {
		return klass.companionObjects.any {
			checkClassOrObjectFunctions(it, klass.name)
		}
	}
}
