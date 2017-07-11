package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiUtil

class LateinitUsage(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			"Usage of lateinit. Using lateinit for property initialization " +
					"is error prone, try using constructor injection or delegation.")

	private val excludeAnnotatedProperties: List<String>
			= valueOrDefault(EXCLUDE_ANNOTATED_PROPERTIES, "").split(",")

	override fun visitProperty(property: KtProperty) {
		if (!isLateinitProperty(property)) {
			return
		}

		if (isExcludedByAnnotation(property)) {
			return
		}

		report(CodeSmell(issue, Entity.from(property)))
	}

	private fun isLateinitProperty(property: KtProperty): Boolean {
		return property.modifierList?.hasModifier(KtTokens.LATEINIT_KEYWORD) ?: false
	}

	private fun isExcludedByAnnotation(property: KtProperty): Boolean {
		return property.annotationEntries
				.map { KtPsiUtil.getShortName(it) }
				.filterNotNull()
				.map { it.toString() }
				.filter { excludeAnnotatedProperties.contains(it) }
				.count() != 0
	}

	companion object {
		const val EXCLUDE_ANNOTATED_PROPERTIES = "excludeAnnotatedProperties"
	}
}
