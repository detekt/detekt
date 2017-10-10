package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isOpen
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.isAbstract
import org.jetbrains.kotlin.psi.psiUtil.isPropertyParameter

/**
 * @author Ivan Balaksha
 */
class UseDataClass(config: Config = Config.empty) : Rule(config) {

	override val issue: Issue = Issue("UseDataClass",
			Severity.Style,
			"Classes that do nothing but hold data should be replaced with a data class.",
			Debt.FIVE_MINS)

	private val defaultFunctionNames = hashSetOf("hashCode", "equals", "toString", "copy")

	override fun visitClass(klass: KtClass) {
		if (isIncorrectClassType(klass)) {
			return
		}
		if (klass.isClosedForExtension() && klass.doesNotExtendAnything()) {
			val declarations = klass.extractDeclarations()
			val properties = declarations.filterIsInstance<KtProperty>()
			val functions = declarations.filterIsInstance<KtNamedFunction>()

			val propertyParameters = klass.extractConstructorPropertyParameters()

			val containsFunctions = functions.none { !defaultFunctionNames.contains(it.name) }
			val containsPropertyOrPropertyParameters = properties.isNotEmpty() || propertyParameters.isNotEmpty()

			if (containsFunctions && containsPropertyOrPropertyParameters) {
				report(CodeSmell(issue, Entity.from(klass), message = ""))
			}
		}
		super.visitClass(klass)
	}

	private fun isIncorrectClassType(klass: KtClass) =
			klass.isData() || klass.isEnum() || klass.isAnnotation() || klass.isSealed()

	private fun KtClass.doesNotExtendAnything() = superTypeListEntries.isEmpty()

	private fun KtClass.isClosedForExtension() = !isAbstract() && !isOpen()

	private fun KtClass.extractDeclarations(): List<KtDeclaration> = getBody()?.declarations ?: emptyList()

	private fun KtClass.extractConstructorPropertyParameters(): List<KtParameter> =
			getPrimaryConstructorParameterList()
					?.parameters
					?.filter { it.isPropertyParameter() } ?: emptyList()
}
