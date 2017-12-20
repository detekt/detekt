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

/**
 *
 * <noncompliant>
 * class MethodNameEqualsClassName {
 *
 *     fun methodNameEqualsClassName() { }
 * }
 *
 * class PropertyNameEqualsClassName {
 *
 *     val propertyEqualsClassName = 0
 * }
 * </noncompliant>
 *
 * @configuration ignoreOverriddenFunction - if overridden functions should be ignored (default: true)
 *
 * @author schalkms
 * @author Marvin Ramin
 */
class MemberNameEqualsClassName(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style,
			"A method should not given the same name as its parent class or object.",
			Debt.FIVE_MINS)

	private val classMessage = "A method is named after the class. This might result in confusion. " +
			"Either rename the method or change it to a constructor."
	private val objectMessage = "A method is named after the class object. " +
					"This might result in confusion. Please rename the method."

	private val ignoreOverriddenFunction = valueOrDefault(IGNORE_OVERRIDDEN_FUNCTION, true)

	override fun visitClass(klass: KtClass) {
		if (!klass.isInterface()) {
			checkClassOrObjectFunctions(klass, klass.name, classMessage)
 			checkCompanionObjectFunctions(klass)
		}
		super.visitClass(klass)
	}

	override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
		if (!declaration.isCompanion()) {
			checkClassOrObjectFunctions(declaration, declaration.name, objectMessage)
		}
		super.visitObjectDeclaration(declaration)
	}

	private fun checkClassOrObjectFunctions(klassOrObject: KtClassOrObject, name: String?, message: String) {
		val body = klassOrObject.getBody() ?: return
		var functions = body.children.filterIsInstance<KtNamedFunction>()
		if (ignoreOverriddenFunction) {
			functions = functions.filter { !it.isOverridden() }
		}
		functions
				.filter { it.name?.equals(name, ignoreCase = true) == true }
				.forEach { report(CodeSmell(issue, Entity.from(it), message)) }
		body.properties
				.filter { it.name?.equals(name, ignoreCase = true) == true }
				.forEach { report(CodeSmell(issue, Entity.from(it), message)) }
	}

	private fun checkCompanionObjectFunctions(klass: KtClass) {
		klass.companionObjects.forEach {
			checkClassOrObjectFunctions(it, klass.name, classMessage)
		}
	}

	companion object {
		const val IGNORE_OVERRIDDEN_FUNCTION = "ignoreOverriddenFunction"
	}
}
