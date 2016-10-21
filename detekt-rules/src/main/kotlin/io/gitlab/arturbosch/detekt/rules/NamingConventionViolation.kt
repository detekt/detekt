package io.gitlab.arturbosch.detekt.rules

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Location
import io.gitlab.arturbosch.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtVariableDeclaration

/**
 * @author Artur Bosch
 */
class NamingConventionViolation(config: Config = Config.EMPTY) : Rule("NamingConventionViolation", Severity.Style, config) {

	private val variablePattern = Regex("^(_)?[a-z$][a-zA-Z$0-9]*$")
	private val methodPattern = Regex("^[a-z$][a-zA-Z$0-9]*$")
	private val classPattern = Regex("^[A-Z$][a-zA-Z$]*$")

	override fun visitNamedDeclaration(declaration: KtNamedDeclaration) {
		if (declaration.nameAsSafeName.isSpecial) return
		declaration.nameIdentifier?.parent?.javaClass?.let {
			val name = declaration.nameAsSafeName.asString()
			if (declaration is KtVariableDeclaration && !name.matches(variablePattern)) {
				addFindings(CodeSmell(id, Location.from(declaration)))
			}
			if (declaration is KtNamedFunction && !name.matches(methodPattern)) {
				addFindings(CodeSmell(id, Location.from(declaration)))
			}
			if (declaration is KtClassOrObject && !name.matches(classPattern)) {
				addFindings(CodeSmell(id, Location.from(declaration)))
			}
		}
		super.visitNamedDeclaration(declaration)
	}

}