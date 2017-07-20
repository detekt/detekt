package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.preprocessor.typeReferenceName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClass

class LateinitUsage(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Usage of lateinit. Using lateinit for property initialization " +
					"is error prone, try using constructor injection or delegation.")

	private val excludeAnnotatedProperties: List<String>
			= valueOrDefault(EXCLUDE_ANNOTATED_PROPERTIES, "")
					.split(",")
					.map { it.trim() }
					.filter { it.isNotBlank() }
					.map { it.removeSuffix("*") }

	private val ignoreOnClassesPattern = Regex(valueOrDefault(IGNORE_ON_CLASSES_PATTERN, ""))

	private var properties = mutableListOf<KtProperty>()

	override fun visitProperty(property: KtProperty) {
		if (isLateinitProperty(property)) {
			properties.add(property)
		}
	}

	override fun visit(root: KtFile) {
		properties = mutableListOf<KtProperty>()

		super.visit(root)

		val resolvedAnnotations = root.importList
				?.imports
				?.filterNot { it.isAllUnder }
				?.map { it.importedFqName?.asString() }
				?.filterNotNull()
				?.map { Pair(it.split(".").last(), it) }
				?.toMap()

		properties.filterNot { isExcludedByAnnotation(it, resolvedAnnotations) }
				.filterNot { it.containingClass()?.name?.matches(ignoreOnClassesPattern) ?: false }
				.forEach {
					report(CodeSmell(issue, Entity.from(it)))
				}
	}

	private fun isLateinitProperty(property: KtProperty)
			= property.modifierList?.hasModifier(KtTokens.LATEINIT_KEYWORD) ?: false

	private fun isExcludedByAnnotation(property: KtProperty, resolvedAnnotations: Map<String, String>?)
			= property.annotationEntries
					.map {
						val shortName = it.typeReferenceName
						resolvedAnnotations?.get(shortName) ?: shortName
					}
					.filterNotNull()
					.none { annotationFqn ->
						excludeAnnotatedProperties.none { annotationFqn.contains(it) }
					}

	companion object {
		const val EXCLUDE_ANNOTATED_PROPERTIES = "excludeAnnotatedProperties"
		const val IGNORE_ON_CLASSES_PATTERN = "ignoreOnClassesPattern"
	}
}
