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
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
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
 * @configuration allowedNames - unused private member names matching this regex are ignored
 * (default: "(_|ignored|expected)")
 *
 * @author Marvin Ramin
 * @author Artur Bosch
 * @author schalkms
 */
class UnusedPrivateMember(config: Config = Config.empty) : Rule(config) {

	companion object {
		const val ALLOWED_NAMES_PATTERN = "allowedNames"
	}

	override val issue: Issue = Issue("UnusedPrivateMember",
			Severity.Maintainability,
			"Private member is unused.",
			Debt.FIVE_MINS,
			aliases = setOf("UNUSED_VARIABLE"))

	private val allowedNames = Regex(valueOrDefault(ALLOWED_NAMES_PATTERN, "(_|ignored|expected)"))

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		if ((classOrObject as? KtClass)?.isInterface() == true) {
			return
		}

		val propertyVisitor = UnusedPropertyVisitor(allowedNames)
		classOrObject.accept(propertyVisitor)

		propertyVisitor.getUnusedProperties().forEach {
			report(CodeSmell(issue, Entity.from(it.value), "Private property ${it.key} is unused."))
		}

		super.visitClassOrObject(classOrObject)
	}

	class UnusedPropertyVisitor(private val allowedNames: Regex) : DetektVisitor() {

		private val properties = mutableMapOf<String, KtElement>()
		private val nameAccesses = mutableSetOf<String>()

		fun getUnusedProperties(): Map<String, KtElement> {
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
					for (variable in destructuringDeclaration.entries) {
						checkAllowedNames(variable)
					}
				} else {
					checkAllowedNames(parameter)
				}
			}
		}

		private fun checkAllowedNames(it: KtNamedDeclaration) {
			val name = it.nameAsSafeName.identifier
			if (!allowedNames.matches(name)) {
				properties[name] = it
			}
		}

		override fun visitProperty(property: KtProperty) {
			if ((property.isPrivate() && property.isNonNestedMember())
					|| property.isLocal) {
				checkAllowedNames(property)
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

	/*
	* Here begins the common part for unused private functions and parameters.
	*/

	override fun postVisit(root: KtFile) {
		getUnusedFunctions().forEach {
			report(CodeSmell(issue, Entity.from(it.value), "Private function ${it.key} is unused."))
		}
		unusedParameters.forEach {
			report(CodeSmell(issue, Entity.from(it.value), "Function parameter ${it.key} is unused."))
		}
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.isPrivate() && !function.isOverridden()) {
			collectFunction(function)
			collectParameters(function)
		}

		super.visitNamedFunction(function)
	}

	/*
	* Here starts the unused parameters part.
	*/
	private var unusedParameters: MutableMap<String, KtParameter> = mutableMapOf()

	private fun collectParameters(function: KtNamedFunction) {
		function.valueParameterList?.parameters?.forEach {
			val name = it.nameAsSafeName.identifier
			if (!allowedNames.matches(name)) {
				unusedParameters[name] = it
			}
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

		unusedParameters = unusedParameters.filterTo(mutableMapOf()) { it.key !in localProperties }
	}

	/*
	* Here starts the unused private functions part.
	*
	* We need to collect all private function declarations and references to these functions
	* for the whole file as Kotlin allows to access private and internal object declarations
	* from everywhere in the file.
	*/

	private val callExpressions = mutableSetOf<String>()
	private val functions = mutableMapOf<String, KtFunction>()

	private fun collectFunction(function: KtNamedFunction) {
		val name = function.nameAsSafeName.identifier
		if (!allowedNames.matches(name)) {
			functions[name] = function
		}
	}

	private fun getUnusedFunctions(): Map<String, KtFunction> {
		for (call in callExpressions) {
			if (functions.isEmpty()) {
				break
			}
			functions.remove(call)
		}
		return functions
	}

	override fun visitReferenceExpression(expression: KtReferenceExpression) {
		super.visitReferenceExpression(expression)
		when (expression) {
			is KtOperationReferenceExpression -> callExpressions.add(expression.getReferencedName())
			is KtNameReferenceExpression -> callExpressions.add(expression.getReferencedName())
		}
	}

	override fun visitCallExpression(expression: KtCallExpression) {
		super.visitCallExpression(expression)
		val calledMethodName = expression.referenceExpression()?.text

		if (calledMethodName != null) {
			callExpressions.add(calledMethodName)
		}
	}
}
