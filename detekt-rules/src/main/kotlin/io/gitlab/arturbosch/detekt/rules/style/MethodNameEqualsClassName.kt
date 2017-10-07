package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isOverridden
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

	private val ignoreOverriddenFunction = valueOrDefault(IGNORE_OVERRIDDEN_FUNCTION, true)

	override fun visitClass(klass: KtClass) {
		if (!klass.isInterface()) {
			checkClassOrObjectFunctions(klass, klass.name)
 			checkCompanionObjectFunctions(klass)
		}
		super.visitClass(klass)
	}

	override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
		if (!declaration.isCompanion()) {
			checkClassOrObjectFunctions(declaration, declaration.name, objectIssue)
		}
		super.visitObjectDeclaration(declaration)
	}

	private fun checkClassOrObjectFunctions(klassOrObject: KtClassOrObject, name: String?, issue: Issue = this.issue) {
		val children = klassOrObject.getBody()?.children
		var functions = children?.filterIsInstance<KtNamedFunction>()
		if (ignoreOverriddenFunction) {
			functions = functions?.filter { !it.isOverridden() }
		}
		functions?.forEach {
			if (it.name == name) report(CodeSmell(issue, Entity.from(it)))
		}
	}

	private fun checkCompanionObjectFunctions(klass: KtClass) {
		return klass.companionObjects.forEach {
			checkClassOrObjectFunctions(it, klass.name)
		}
	}

	companion object {
		const val IGNORE_OVERRIDDEN_FUNCTION = "ignoreOverriddenFunction"
	}
}
