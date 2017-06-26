package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.*
import org.jetbrains.kotlin.com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.utils.ifEmpty

class LateinitUsage(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName, Severity.Style, "Usage of lateinit. Using lateinit for property initialization is error prone, try using constructor injection or delegation.")

	private val excludeAnnotatedProperties: List<String> = valueOrDefault(EXCLUDE_ANNOTATED_PROPERTIES, "").split("|")

	lateinit var test: String

	override fun visitProperty(property: KtProperty) {
		val isLateinitProperty = property.modifierList?.hasModifier(KtTokens.LATEINIT_KEYWORD) ?: false
		if (!isLateinitProperty) {
			return
		}

		val isExcluded = property.annotationEntries
				.map { KtPsiUtil.getShortName(it).toString() }
				.filter { excludeAnnotatedProperties.contains(it) }
				.count() != 0
		if (isExcluded) {
			return
		}

		report(CodeSmell(issue, Entity.from(property)))
	}

	companion object {
		const val EXCLUDE_ANNOTATED_PROPERTIES = "excludeAnnotatedProperties"
	}
}