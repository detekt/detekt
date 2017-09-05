package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
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
			"Classes that do nothing but hold data should be replaced with a data class.")

	private val defaultFunctionNames = listOf("hashCode", "equals", "toString", "copy")

	override fun visitClass(klass: KtClass) {
		if (!klass.isData() && klass.isClosedForExtension() && klass.doesNotExtendAnything()) {

			val declarations = klass.extractDeclarations()
			val properties = declarations.filterIsInstance<KtProperty>()
			val functions = declarations.filterIsInstance<KtNamedFunction>()

			val propertyParameters = klass.extractConstructorPropertyParameters()

			val containsFunctions = functions.none { !defaultFunctionNames.contains(it.name) }
			val containsPropertyOrPropertyParameters = properties.isNotEmpty() || propertyParameters.isNotEmpty()

			if (containsFunctions && containsPropertyOrPropertyParameters) {
				report(CodeSmell(issue, Entity.from(klass)))
			}
		}
		super.visitClass(klass)
	}

	private fun KtClass.doesNotExtendAnything() = superTypeListEntries.isEmpty()

	private fun KtClass.isClosedForExtension() = !isAbstract() && !hasModifier(KtTokens.OPEN_KEYWORD)

	private fun KtClass.extractDeclarations(): List<KtDeclaration> = getBody()?.declarations ?: emptyList()

	private fun KtClass.extractConstructorPropertyParameters(): List<KtParameter> =
			getPrimaryConstructorParameterList()
					?.parameters
					?.filter { it.isPropertyParameter() } ?: emptyList()
}
