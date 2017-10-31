package io.gitlab.arturbosch.detekt.rules.documentation

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.rules.isPublicNotOverridden
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtEnumEntry
import org.jetbrains.kotlin.psi.KtObjectDeclaration

/**
 * @author Artur Bosch
 */
class UndocumentedPublicClass(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Maintainability,
			"Public classes, interfaces and objects require documentation.")

	private val searchInNestedClass = valueOrDefault(SEARCH_IN_NESTED_CLASS, true)
	private val searchInInnerClass = valueOrDefault(SEARCH_IN_INNER_CLASS, true)
	private val searchInInnerObject = valueOrDefault(SEARCH_IN_INNER_OBJECT, true)
	private val searchInInnerInterface = valueOrDefault(SEARCH_IN_INNER_INTERFACE, true)

	override fun visitClass(klass: KtClass) {
		if (requiresDocumentation(klass)) {
			reportIfUndocumented(klass)
		}

		super.visitClass(klass)
	}

	private fun requiresDocumentation(
			klass: KtClass) = klass.isTopLevel() || klass.isInnerClass() || klass.isNestedClass() || klass.isInnerInterface()

	override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
		if (declaration.isCompanionWithoutName() || declaration.isLocal || !searchInInnerObject) {
			return
		}

		reportIfUndocumented(declaration)
		super.visitObjectDeclaration(declaration)
	}

	private fun reportIfUndocumented(element: KtClassOrObject) {
		if (element.isPublicNotOverridden() && element.notEnumEntry() && element.docComment == null) {
			report(CodeSmell(issue, Entity.Companion.from(element)))
		}
	}

	private fun KtObjectDeclaration.isCompanionWithoutName() =
			isCompanion() && nameAsSafeName.asString() == "Companion"

	private fun KtClass.isNestedClass() = !isInterface() && !isTopLevel() && !isInner() && searchInNestedClass

	private fun KtClass.isInnerClass() = !isInterface() && isInner() && searchInInnerClass

	private fun KtClass.isInnerInterface() = !isTopLevel() && isInterface() && searchInInnerInterface

	private fun KtClassOrObject.notEnumEntry() = this::class != KtEnumEntry::class

	companion object {
		const val SEARCH_IN_NESTED_CLASS = "searchInNestedClass"
		const val SEARCH_IN_INNER_CLASS = "searchInInnerClass"
		const val SEARCH_IN_INNER_OBJECT = "searchInInnerObject"
		const val SEARCH_IN_INNER_INTERFACE = "searchInInnerInterface"
	}
}
