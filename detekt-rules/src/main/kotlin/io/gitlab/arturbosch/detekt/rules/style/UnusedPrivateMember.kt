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
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression

/**
 * Reports unused private properties, function parameters and functions.
 * If private properties are unused they should be removed. Otherwise this dead code
 * can lead to confusion and potential bugs.
 *
 * @author Marvin Ramin
 * @author Artur Bosch
 */
class UnusedPrivateMember(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue("UnusedPrivateMember",
			Severity.Maintainability,
			"Private member is unused.",
			Debt.FIVE_MINS,
			aliases = setOf("UNUSED_VARIABLE"))

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		if ((classOrObject as? KtClass)?.isInterface() == true) {
			return
		}

		val propertyVisitor = UnusedPropertyVisitor()
		classOrObject.accept(propertyVisitor)

		propertyVisitor.getProperties().forEach {
			report(CodeSmell(issue, Entity.from(it.value), "Private property ${it.key} is unused."))
		}

		val functionParameterVisitor = UnusedFunctionParameterVisitor()
		classOrObject.accept(functionParameterVisitor)

		functionParameterVisitor.parameters.forEach {
			report(CodeSmell(issue, Entity.from(it.value), "Function parameter ${it.key} is unused."))
		}

		val functionVisitor = UnusedFunctionVisitor()
		classOrObject.accept(functionVisitor)

		functionVisitor.getFunctions().forEach {
			report(CodeSmell(issue, Entity.from(it.value), "Private function ${it.key} is unused."))
		}

		super.visitClassOrObject(classOrObject)
	}

	class UnusedFunctionParameterVisitor : DetektVisitor() {

		var parameters: MutableMap<String, KtParameter> = mutableMapOf()

		override fun visitNamedFunction(function: KtNamedFunction) {
			if (!function.isPrivate() || function.isOverridden()) {
				return
			}

			function.valueParameterList?.parameters?.forEach {
				val name = it.nameAsSafeName.identifier
				parameters[name] = it
			}

			val localProperties = mutableListOf<String>()
			function.accept(object : DetektVisitor() {
				override fun visitProperty(property: KtProperty) {
					if (property.isLocal) {
						val name = property.nameAsSafeName.identifier
						localProperties.add(name)
					}
					super.visitProperty(property)
				}

				override fun visitReferenceExpression(expression: KtReferenceExpression) {
					localProperties.add(expression.text)
					super.visitReferenceExpression(expression)
				}
			})

			parameters = parameters.filterTo(mutableMapOf()) { it.key !in localProperties }
		}
	}

	class UnusedPropertyVisitor : DetektVisitor() {

		private val properties = mutableMapOf<String, KtElement>()
		private val nameAccesses = mutableSetOf<String>()

		fun getProperties(): Map<String, KtElement> {
			for (access in nameAccesses) {
				if (properties.isEmpty()) {
					break
				}
				properties.remove(access)
			}
			return properties
		}

		override fun visitParameter(parameter: KtParameter) {
			super.visitParameter(parameter)
			if (parameter.isLoopParameter) {
				val destructuringDeclaration = parameter.destructuringDeclaration
				if (destructuringDeclaration != null) {
					destructuringDeclaration.entries.forEach {
						val name = it.nameAsSafeName.identifier
						properties[name] = it
					}
				} else {
					val name = parameter.nameAsSafeName.identifier
					properties[name] = parameter
				}
			}
		}

		override fun visitProperty(property: KtProperty) {
			if ((property.isPrivate() && property.isNonNestedMember())
					|| property.isLocal) {
				val name = property.nameAsSafeName.identifier
				properties[name] = property
			}
			super.visitProperty(property)
		}

		private fun KtProperty.isNonNestedMember() = isMember
				&& parent // KtClassBody
				?.parent  // KtClassOrObject
				?.parent is KtFile

		override fun visitReferenceExpression(expression: KtReferenceExpression) {
			nameAccesses.add(expression.text)
			super.visitReferenceExpression(expression)
		}
	}

	class UnusedFunctionVisitor : DetektVisitor() {
		private val callExpressions = mutableSetOf<String>()
		private val functions = mutableMapOf<String, KtFunction>()

		fun getFunctions(): Map<String, KtFunction> {
			for (call in callExpressions) {
				if (functions.isEmpty()) {
					break
				}
				functions.remove(call)
			}
			return functions
		}

		override fun visitNamedFunction(function: KtNamedFunction) {
			if (function.isPrivate() && !function.isOverridden()) {
				val name = function.nameAsSafeName.identifier
				functions[name] = function
			}

			super.visitNamedFunction(function)
		}

		override fun visitCallExpression(expression: KtCallExpression) {
			super.visitCallExpression(expression)
			val calledMethodName = expression.referenceExpression()?.text

			if (calledMethodName != null) {
				callExpressions.add(calledMethodName)
			}
		}
	}
}
