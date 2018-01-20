package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.getNonStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression

/**
 * Reports unused private properties. If private properties are unused they should be removed. Otherwise this dead code
 * can lead to confusion and potential bugs.
 *
 * @author Marvin Ramin
 */
class UnusedPrivateMember(config: Config = Config.empty) : Rule(config) {
	override val issue: Issue = Issue("UnusedPrivateMember",
			Severity.Maintainability,
			"Private member is unused.")

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		val propertyVisitor = UnusedPropertyVisitor()
		classOrObject.accept(propertyVisitor)

		propertyVisitor.properties.forEach {
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
			super.visitNamedFunction(function)

			function.valueParameterList?.parameters?.forEach {
				val name = it.name ?: throw IllegalStateException("Value parameter should have a name.")
				parameters.put(name, it)
			}

			val localProperties = mutableListOf<String>()
			function.accept(object : DetektVisitor() {
				override fun visitProperty(property: KtProperty) {
					if (property.isLocal) {
						val name = property.name ?: throw IllegalStateException("Properties should have a name")
						localProperties.add(name)
					}
					super.visitProperty(property)
				}

				override fun visitReferenceExpression(expression: KtReferenceExpression) {
					localProperties.add(expression.text)
					super.visitReferenceExpression(expression)
				}
			})

			parameters.forEach {
				if (localProperties.contains(it.key)) {
					parameters.remove(it.key)
				}
			}
		}
	}

	class UnusedPropertyVisitor : DetektVisitor() {
		val properties = mutableMapOf<String, KtElement>()

		override fun visitProperty(property: KtProperty) {
			if ((property.isPrivate() && property.isMember) || property.isLocal) {
				val name = property.name ?: throw IllegalStateException("Private properties should have a name")
				properties.put(name, property)
			}
			super.visitProperty(property)
		}

		override fun visitReferenceExpression(expression: KtReferenceExpression) {
			val name = expression.text
			if (properties.contains(name)) {
				properties.remove(name)
			}

			super.visitReferenceExpression(expression)
		}
	}

	class UnusedFunctionVisitor : DetektVisitor() {
		private val callExpressions = mutableMapOf<KtFunction?, String>()
		private val functions = mutableMapOf<String, KtFunction>()

		fun getFunctions(): Map<String, KtFunction> {
			val unusedFunctions = mutableMapOf<String, KtFunction>()
			var change = true
			while (change) {
				unusedFunctions.putAll(functions.filterNot { callExpressions.containsValue(it.key) })
				val validExpressions = callExpressions.filterNot { unusedFunctions.containsValue(it.key) }
				change = validExpressions != callExpressions
				callExpressions.clear()
				callExpressions.putAll(validExpressions)
			}

			return unusedFunctions
		}

		override fun visitNamedFunction(function: KtNamedFunction) {
			if (!function.isPrivate()) {
				return
			}

			val name = function.name ?: throw IllegalStateException("Functions should have a name.")
			functions.put(name, function)
			super.visitNamedFunction(function)
		}

		override fun visitCallExpression(expression: KtCallExpression) {
			super.visitCallExpression(expression)
			val calledMethodName = expression.referenceExpression()?.text
			val function = expression.getNonStrictParentOfType(KtFunction::class.java)

			if (calledMethodName != null) {
				callExpressions.put(function, calledMethodName)
			}
		}
	}
}
