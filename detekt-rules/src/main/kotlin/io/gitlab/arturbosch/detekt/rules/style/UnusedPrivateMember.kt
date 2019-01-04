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
import io.gitlab.arturbosch.detekt.rules.hasAnnotationWithValue
import io.gitlab.arturbosch.detekt.rules.isAbstract
import io.gitlab.arturbosch.detekt.rules.isExternal
import io.gitlab.arturbosch.detekt.rules.isInterface
import io.gitlab.arturbosch.detekt.rules.isMainFunction
import io.gitlab.arturbosch.detekt.rules.isOpen
import io.gitlab.arturbosch.detekt.rules.isOperator
import io.gitlab.arturbosch.detekt.rules.isOverride
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtCallExpression
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
		const val SUPPRESS_ANNOTATION = "Suppress"
		const val SUPPRESS_UNUSED_PARAMETER = "\"UNUSED_PARAMETER\""
		const val SUPPRESS_UNUSED_PROPERTY = "\"unused\""
		const val SUPPRESS_UNUSED_FUNCTION = "\"unused\""
	}

	override val defaultRuleIdAliases: Set<String> = setOf("UNUSED_VARIABLE")

	override val issue: Issue = Issue("UnusedPrivateMember",
			Severity.Maintainability,
			"Private member is unused.",
			Debt.FIVE_MINS)

	private val allowedNames by LazyRegex(ALLOWED_NAMES_PATTERN, "(_|ignored|expected|serialVersionUID)")

	override fun visit(root: KtFile) {
		super.visit(root)

		if (!root.hasSuppressUnusedFunctionAnnotation()) {
			root.acceptUnusedMemberVisitor(UnusedFunctionVisitor(allowedNames))
		}

		if (!root.hasSuppressUnusedParameterAnnotation()) {
			root.acceptUnusedMemberVisitor(UnusedParameterVisitor(allowedNames))
		}

		if (!root.hasSuppressUnusedPropertyAnnotation()) {
			root.acceptUnusedMemberVisitor(UnusedPropertyVisitor(allowedNames))
		}
	}

	private fun KtFile.acceptUnusedMemberVisitor(visitor: UnusedMemberVisitor) {
		accept(visitor)
		visitor.getUnusedReports(issue).forEach { report(it) }
	}
}

private abstract class UnusedMemberVisitor(protected val allowedNames: Regex) : DetektVisitor() {

	protected var suppressReports = false

	abstract fun getUnusedReports(issue: Issue): List<CodeSmell>

	final override fun visitClassOrObject(classOrObject: KtClassOrObject) {
		runThenRestoreState {
			if (shouldSuppressReportsForClassOrObject(classOrObject)) {
				suppressReports = true
			}

			super.visitClassOrObject(classOrObject)
		}
	}

	abstract fun shouldSuppressReportsForClassOrObject(classOrObject: KtClassOrObject): Boolean

	protected inline fun runThenRestoreState(block: () -> Unit) {
		val oldSuppressState = suppressReports
		block()
		suppressReports = oldSuppressState
	}
}

private class UnusedFunctionVisitor(allowedNames: Regex) : UnusedMemberVisitor(allowedNames) {

	private val callExpressions = mutableSetOf<String>()
	private val functions = mutableMapOf<String, KtFunction>()

	override fun getUnusedReports(issue: Issue): List<CodeSmell> {
		for (call in callExpressions) {
			if (functions.isEmpty()) {
				break
			}
			functions.remove(call)
		}
		return functions.map { CodeSmell(issue, Entity.from(it.value), "Private function ${it.key} is unused.") }
	}

	override fun shouldSuppressReportsForClassOrObject(classOrObject: KtClassOrObject): Boolean {
		return classOrObject.isInterface() || classOrObject.hasSuppressUnusedFunctionAnnotation()
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		if (function.isPrivate()) {
			collectFunction(function)
		}

		super.visitNamedFunction(function)
	}

	private fun collectFunction(function: KtNamedFunction) {
		val name = function.nameAsSafeName.identifier
		if (!allowedNames.matches(name) &&
				!function.hasSuppressUnusedFunctionAnnotation() &&
				!suppressReports
		) {
			functions[name] = function
		}
	}

	/*
	 * We need to collect all private function declarations and references to these functions
	 * for the whole file as Kotlin allows to access private and internal object declarations
	 * from everywhere in the file.
	 */

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

private class UnusedParameterVisitor(allowedNames: Regex) : UnusedMemberVisitor(allowedNames) {

	private var unusedParameters: MutableMap<String, KtParameter> = mutableMapOf()

	override fun getUnusedReports(issue: Issue): List<CodeSmell> {
		return unusedParameters.map { CodeSmell(issue, Entity.from(it.value), "Function parameter ${it.key} is unused.") }
	}

	override fun shouldSuppressReportsForClassOrObject(classOrObject: KtClassOrObject): Boolean {
		return classOrObject.isInterface() || classOrObject.hasSuppressUnusedParameterAnnotation()
	}

	override fun visitNamedFunction(function: KtNamedFunction) {
		runThenRestoreState {
			if (!function.isRelevant() || function.hasSuppressUnusedParameterAnnotation()) {
				suppressReports = true
			}

			collectParameters(function)

			super.visitNamedFunction(function)
		}
	}

	private fun collectParameters(function: KtNamedFunction) {
		function.valueParameterList?.parameters?.forEach { parameter ->
			val name = parameter.nameAsSafeName.identifier
			if (!allowedNames.matches(name) &&
					!suppressReports &&
					!parameter.hasSuppressUnusedParameterAnnotation()
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

	private fun KtNamedFunction.isRelevant() = !isAllowedToHaveUnusedParameters()

	private fun KtNamedFunction.isAllowedToHaveUnusedParameters() =
			isAbstract() || isOpen() || isOverride() || isOperator() || isMainFunction() || isExternal()
}

private class UnusedPropertyVisitor(allowedNames: Regex) : UnusedMemberVisitor(allowedNames) {

	private val properties = mutableSetOf<KtNamedDeclaration>()
	private val nameAccesses = mutableSetOf<String>()

	override fun getUnusedReports(issue: Issue): List<CodeSmell> {
		return properties
				.filter { it.nameAsSafeName.identifier !in nameAccesses }
				.map { CodeSmell(issue, Entity.from(it), "Private property ${it.nameAsSafeName.identifier} is unused.") }
	}

	override fun shouldSuppressReportsForClassOrObject(classOrObject: KtClassOrObject): Boolean {
		return classOrObject.hasSuppressUnusedPropertyAnnotation()
	}

	override fun visitParameter(parameter: KtParameter) {
		super.visitParameter(parameter)
		if (parameter.isLoopParameter) {
			val destructuringDeclaration = parameter.destructuringDeclaration
			if (destructuringDeclaration != null) {
				for (variable in destructuringDeclaration.entries) {
					maybeAddUnusedProperty(variable)
				}
			} else {
				maybeAddUnusedProperty(parameter)
			}
		}
	}

	override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
		runThenRestoreState {
			if (constructor.hasSuppressUnusedPropertyAnnotation()) {
				suppressReports = true
			}

			super.visitPrimaryConstructor(constructor)
			constructor.valueParameters
					.filter { it.isPrivate() || !it.hasValOrVar() }
					.forEach { maybeAddUnusedProperty(it) }
		}
	}

	override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
		runThenRestoreState {
			if (constructor.hasSuppressUnusedPropertyAnnotation()) {
				suppressReports = true
			}

			super.visitSecondaryConstructor(constructor)
			constructor.valueParameters.forEach { maybeAddUnusedProperty(it) }
		}
	}

	private fun maybeAddUnusedProperty(it: KtNamedDeclaration) {
		if (!allowedNames.matches(it.nameAsSafeName.identifier) &&
				!suppressReports &&
				!it.hasSuppressUnusedPropertyAnnotation()) {
			properties.add(it)
		}
	}

	override fun visitProperty(property: KtProperty) {
		if (property.isPrivate() && property.isMemberOrTopLevel() || property.isLocal) {
			maybeAddUnusedProperty(property)
		}
		super.visitProperty(property)
	}

	private fun KtProperty.isMemberOrTopLevel() = isMember || isTopLevel

	override fun visitReferenceExpression(expression: KtReferenceExpression) {
		nameAccesses.add(expression.text)
		super.visitReferenceExpression(expression)
	}
}

private fun KtAnnotated.hasSuppressUnusedPropertyAnnotation(): Boolean {
	return hasAnnotationWithValue(UnusedPrivateMember.SUPPRESS_ANNOTATION, UnusedPrivateMember.SUPPRESS_UNUSED_PROPERTY)
}

private fun KtAnnotated.hasSuppressUnusedParameterAnnotation(): Boolean {
	return hasAnnotationWithValue(UnusedPrivateMember.SUPPRESS_ANNOTATION, UnusedPrivateMember.SUPPRESS_UNUSED_PARAMETER)
}

private fun KtAnnotated.hasSuppressUnusedFunctionAnnotation(): Boolean {
	return hasAnnotationWithValue(UnusedPrivateMember.SUPPRESS_ANNOTATION, UnusedPrivateMember.SUPPRESS_UNUSED_FUNCTION)
}
