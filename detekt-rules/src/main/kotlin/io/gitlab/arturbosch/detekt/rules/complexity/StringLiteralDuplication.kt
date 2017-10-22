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

class StringLiteralDuplication(
		config: Config = Config.empty,
		threshold: Int = DEFAULT_DUPLICATION
) : ThresholdRule(config, threshold) {

	override val issue = Issue(javaClass.simpleName, Severity.Maintainability,
			"Multiple occurrences of the same string literal within a single file detected.",
			Debt.FIVE_MINS)

	private val ignoreAnnotation = valueOrDefault(IGNORE_ANNOTATION, true)
	private val excludeStringsWithLessThan5Characters = valueOrDefault(EXCLUDE_SHORT_STRING, true)
	private val ignoreStringsRegex = Regex(valueOrDefault(IGNORE_STRINGS_REGEX, "$^"))

	override fun visitKtFile(file: KtFile) {
		val visitor = StringLiteralVisitor()
		file.accept(visitor)
		val type = "SIZE: "
		for ((name, value) in visitor.getLiteralsOverThreshold()) {
			val (main, references) = visitor.entitiesForLiteral(name)
			report(ThresholdedCodeSmell(issue, main, Metric(type + name, value, threshold), references))
		}
	}

	internal inner class StringLiteralVisitor : DetektVisitor() {

		private var literals = HashMap<String, Int>()
		private var literalReferences = HashMap<String, MutableList<KtLiteralStringTemplateEntry>>()
		private val pass: Unit = Unit

		fun getLiteralsOverThreshold(): Map<String, Int> = literals.filterValues { it > threshold }
		fun entitiesForLiteral(literal: String): Pair<Entity, List<Entity>> {
			val references = literalReferences[literal]
			if (references != null && references.isNotEmpty()) {
				val mainEntity = references[0]
				val referenceEntities = references.subList(1, references.size)
				return Entity.from(mainEntity) to referenceEntities.map { Entity.from(it) }
			}
			throw IllegalStateException("No KtElements for literal '$literal' found!")
		}

		override fun visitLiteralStringTemplateEntry(entry: KtLiteralStringTemplateEntry) {
			when {
				ignoreAnnotation && entry.isPartOf(KtAnnotationEntry::class) -> pass
				excludeStringsWithLessThan5Characters && entry.text.length < STRING_EXCLUSION_LENGTH -> pass
				entry.text.matches(ignoreStringsRegex) -> pass
				else -> add(entry)
			}
		}

		private fun add(entry: KtLiteralStringTemplateEntry) {
			val text = entry.text
			literals.compute(text) { _, oldValue -> oldValue?.plus(1) ?: 1 }
			literalReferences.compute(text) { _, entries -> entries?.add(entry); entries ?: mutableListOf(entry) }
		}
	}

	companion object {
		const val DEFAULT_DUPLICATION = 2
		const val STRING_EXCLUSION_LENGTH = 5
		const val IGNORE_ANNOTATION = "ignoreAnnotation"
		const val EXCLUDE_SHORT_STRING = "excludeStringsWithLessThan5Characters"
		const val IGNORE_STRINGS_REGEX = "ignoreStringsRegex"
	}
}
