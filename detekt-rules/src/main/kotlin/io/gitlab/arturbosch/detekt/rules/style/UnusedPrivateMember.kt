package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.LazyRegex
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isAbstract
import io.gitlab.arturbosch.detekt.rules.isExternal
import io.gitlab.arturbosch.detekt.rules.isMainFunction
import io.gitlab.arturbosch.detekt.rules.isOpen
import io.gitlab.arturbosch.detekt.rules.isOperator
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList
import org.jetbrains.kotlin.psi.psiUtil.isPrivate
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression

/**
 * Reports unused private properties, function parameters and functions.
 * If private properties are unused they should be removed. Otherwise this dead code
 * can lead to confusion and potential bugs.
 *
 * @configuration allowedNames - unused private member names matching this regex are ignored
 * (default: "(_|ignored|expected|serialVersionUID)")
 *
 * @author Marvin Ramin
 * @author Artur Bosch
 * @author schalkms
 * @author Andrew Arnott
 */
class UnusedPrivateMember(config: Config = Config.empty) : Rule(config) {

	companion object {
		const val ALLOWED_NAMES_PATTERN = "allowedNames"
	}

	override val defaultRuleIdAliases: Set<String> = setOf("UNUSED_VARIABLE")

	override val issue: Issue = Issue("UnusedPrivateMember",
			Severity.Maintainability,
			"Private member is unused.",
			Debt.FIVE_MINS)

	private val allowedNames by LazyRegex(ALLOWED_NAMES_PATTERN, "(_|ignored|expected|serialVersionUID)")

	override fun visit(root: KtFile) {
		super.visit(root)

		val propertyVisitor = UnusedPropertyVisitor(allowedNames)
		root.accept(propertyVisitor)

		propertyVisitor.getUnusedProperties().forEach {
			report(CodeSmell(issue, Entity.from(it), "Private property ${it.nameAsSafeName.identifier} is unused."))
		}

		val functionVisitor = UnusedFunctionVisitor(allowedNames)
		root.accept(functionVisitor)

		functionVisitor.getUnusedFunctions().forEach {
			report(CodeSmell(issue, Entity.from(it.value), "Private function ${it.key} is unused."))
		}

		val parameterVisitor = UnusedParameterVisitor(allowedNames)
		root.accept(parameterVisitor)

		if (!root.isAnnotatedWithSuppressUnusedParameterWarnings()) {
			parameterVisitor.getUnusedParameters().forEach {
				report(CodeSmell(issue, Entity.from(it.value), "Function parameter ${it.key} is unused."))
			}
		}
	}
}

/*
* Here starts the unused private functions part.
*
* We need to collect all private function declarations and references to these functions
* for the whole file as Kotlin allows to access private and internal object declarations
* from everywhere in the file.
*/
private class UnusedFunctionVisitor(private val allowedNames: Regex) : DetektVisitor() {
	private val callExpressions = mutableSetOf<String>()
	private val functions = mutableMapOf<String, KtFunction>()

	fun getUnusedFunctions(): Map<String, KtFunction> {
		for (call in callExpressions) {
			if (functions.isEmpty()) {
				break
			}
			functions.remove(call)
		}
		return functions
	}

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		if ((classOrObject as? KtClass)?.isInterface() == true) {
			return
		}

		super.visitClassOrObject(classOrObject)
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.isPrivate()) {
			collectFunction(function)
		}

		super.visitNamedFunction(function)
	}

	private fun collectFunction(function: KtNamedFunction) {
		val name = function.nameAsSafeName.identifier
		if (!allowedNames.matches(name)) {
			functions[name] = function
		}
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

/*
* Here starts the unused parameters part.
*/
private class UnusedParameterVisitor(private val allowedNames: Regex) : DetektVisitor() {
	private var unusedParameters: MutableMap<String, KtParameter> = mutableMapOf()
	private var suppressUnusedParameterReports = false

	fun getUnusedParameters(): Map<String, KtParameter> {
		return unusedParameters
	}

	override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		runThenRestoreState {
			if (classOrObject.isInterface() || classOrObject.isAnnotatedWithSuppressUnusedParameterWarnings()) {
				suppressUnusedParameterReports = true
			}

			super.visitClassOrObject(classOrObject)
		}
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		runThenRestoreState {
			if (!function.isRelevant() || function.isAnnotatedWithSuppressUnusedParameterWarnings()) {
				suppressUnusedParameterReports = true
			}

			collectParameters(function)

			super.visitNamedFunction(function)
		}
	}

	private fun collectParameters(function: KtNamedFunction) {
		function.valueParameterList?.parameters?.forEach { parameter ->
			val name = parameter.nameAsSafeName.identifier
			if (!allowedNames.matches(name) &&
					!suppressUnusedParameterReports &&
					!parameter.isAnnotatedWithSuppressUnusedParameterWarnings()
			) {
				unusedParameters[name] = parameter
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

	private inline fun runThenRestoreState(block: () -> Unit) {
		val oldSuppressState = suppressUnusedParameterReports
		block()
		suppressUnusedParameterReports = oldSuppressState
	}

	private fun KtClassOrObject.isInterface(): Boolean {
		return (this as? KtClass)?.isInterface() == true
	}

	private fun KtNamedFunction.isRelevant() = !isAllowedToHaveUnusedParameters()

	private fun KtNamedFunction.isAllowedToHaveUnusedParameters() =
			isAbstract() || isOpen() || isOverride() || isOperator() || isMainFunction() || isExternal()
}

private class UnusedPropertyVisitor(private val allowedNames: Regex) : DetektVisitor() {

	private val properties = mutableSetOf<KtNamedDeclaration>()
	private val nameAccesses = mutableSetOf<String>()

	fun getUnusedProperties(): List<KtNamedDeclaration> {
		return properties.filter { it.nameAsSafeName.identifier !in nameAccesses }
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

	override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
		super.visitPrimaryConstructor(constructor)
		constructor.valueParameters
				.filter { it.isPrivate() || !it.hasValOrVar() }
				.forEach { checkAllowedNames(it) }
	}

	override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
		super.visitSecondaryConstructor(constructor)
		constructor.valueParameters.forEach { checkAllowedNames(it) }
	}

	private fun checkAllowedNames(it: KtNamedDeclaration) {
		if (!allowedNames.matches(it.nameAsSafeName.identifier)) {
			properties.add(it)
		}
	}

	override fun visitProperty(property: KtProperty) {
		if (property.isPrivate() && property.isMemberOrTopLevel() || property.isLocal) {
			checkAllowedNames(property)
		}
		super.visitProperty(property)
	}

	private fun KtProperty.isMemberOrTopLevel() = isMember || isTopLevel

	override fun visitReferenceExpression(expression: KtReferenceExpression) {
		nameAccesses.add(expression.text)
		super.visitReferenceExpression(expression)
	}
}

private fun KtAnnotated.isAnnotatedWithSuppressUnusedParameterWarnings(): Boolean {
	return annotationEntries.any { it.isSuppressUnusedParameterWarningsAnnotation() }
}

private fun KtAnnotationEntry.isSuppressUnusedParameterWarningsAnnotation(): Boolean {
	return typeReference.isSuppressAnnotation() &&
			valueArgumentList.containsUnusedParameterString()
}

private fun KtTypeReference?.isSuppressAnnotation(): Boolean {
	if (this == null) {
		return false
	}

	val type = typeElement

	return if (type is KtUserType) {
		type.referencedName == "Suppress"
	} else {
		false
	}
}

private fun KtValueArgumentList?.containsUnusedParameterString(): Boolean {
	return this?.arguments
			?.any { it.isSuppressParameterWarningString() }
			?: false
}

private fun KtValueArgument.isSuppressParameterWarningString(): Boolean {
	return text == "\"UNUSED_PARAMETER\""
}
