package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.AnnotationExcluder
import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Excludes
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClass

class LateinitUsage(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Defect,
			"Usage of lateinit. Using lateinit for property initialization " +
					"is error prone, try using constructor injection or delegation.")

	private val excludeAnnotatedProperties = Excludes(valueOrDefault(EXCLUDE_ANNOTATED_PROPERTIES, ""))

	private val ignoreOnClassesPattern = Regex(valueOrDefault(IGNORE_ON_CLASSES_PATTERN, ""))

	private var properties = mutableListOf<KtProperty>()

	override fun visitProperty(property: KtProperty) {
		if (isLateinitProperty(property)) {
			properties.add(property)
		}
	}

	override fun visit(root: KtFile) {
		properties = mutableListOf()

		super.visit(root)

		val annotationExcluder = AnnotationExcluder(root, excludeAnnotatedProperties)

		properties.filterNot { annotationExcluder.shouldExclude(it.annotationEntries) }
				.filterNot { it.containingClass()?.name?.matches(ignoreOnClassesPattern) == true }
				.forEach {
					report(CodeSmell(issue, Entity.from(it)))
				}
	}

	private fun isLateinitProperty(property: KtProperty)
			= property.modifierList?.hasModifier(KtTokens.LATEINIT_KEYWORD) == true

	companion object {
		const val EXCLUDE_ANNOTATED_PROPERTIES = "excludeAnnotatedProperties"
		const val IGNORE_ON_CLASSES_PATTERN = "ignoreOnClassesPattern"
	}
}
