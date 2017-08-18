package io.gitlab.arturbosch.detekt.rules.complexity

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.DetektVisitor
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Metric
import io.gitlab.arturbosch.detekt.api.Severity
import io.gitlab.arturbosch.detekt.api.ThresholdRule
import io.gitlab.arturbosch.detekt.api.ThresholdedCodeSmell
import io.gitlab.arturbosch.detekt.api.isPartOf
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry

class StringLiteralDuplication(config: Config = Config.empty,
							   threshold: Int = StringLiteralDuplication.DEFAULT_DUPLICATION) : ThresholdRule(config, threshold) {

	override val issue = Issue(javaClass.simpleName, Severity.Maintainability,
			"Multiple occurrences of the same string literal within a single kt file detected.",
			Debt.FIVE_MINS)

	private val ignoreAnnotation = valueOrDefault(IGNORE_ANNOTATION, true)
	private val excludeStringsWithLessThan5Characters = valueOrDefault(EXCLUDE_SHORT_STRING, true)
	private val ignoreStringsRegex = Regex(valueOrDefault(IGNORE_STRINGS_REGEX, "$^"))

	override fun visitKtFile(file: KtFile) {
		val visitor = StringLiteralVisitor()
		file.accept(visitor)
		val type = "SIZE: "
		visitor.getLiteralsOverThreshold().forEach {
			report(ThresholdedCodeSmell(issue, Entity.from(file), Metric(type + it.key, it.value, threshold)))
		}
	}

	internal inner class StringLiteralVisitor : DetektVisitor() {

		private var literals = HashMap<String, Int>()
		private val pass: Unit = Unit

		fun getLiteralsOverThreshold(): Map<String, Int> {
			return literals.filterValues { it > threshold }
		}

		override fun visitLiteralStringTemplateEntry(entry: KtLiteralStringTemplateEntry) {
			val text = entry.text
			when {
				ignoreAnnotation &&	entry.isPartOf(KtAnnotationEntry::class) -> pass
				excludeStringsWithLessThan5Characters && text.length < 5 -> pass
				text.matches(ignoreStringsRegex) -> pass
				else -> add(text)
			}
		}

		private fun add(text: String) {
			literals.put(text, literals.getOrDefault(text, 0) + 1)
		}
	}

	companion object {
		const val DEFAULT_DUPLICATION = 2
		const val IGNORE_ANNOTATION = "ignoreAnnotation"
		const val EXCLUDE_SHORT_STRING = "excludeStringsWithLessThan5Characters"
		const val IGNORE_STRINGS_REGEX = "ignoreStringsRegex"
	}
}
