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
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry

class StringLiteralDuplication(config: Config = Config.empty,
							   threshold: Int = DEFAULT_DUPLICATION) : ThresholdRule(config, threshold) {

	override val issue = Issue(javaClass.simpleName, Severity.Maintainability,
			"Multiple occurrences of the same string literal within a single kt file detected.",
			Debt.FIVE_MINS)

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

		fun getLiteralsOverThreshold(): Map<String, Int> {
			return literals.filterValues { it > threshold }
		}

		override fun visitLiteralStringTemplateEntry(entry: KtLiteralStringTemplateEntry) {
			val text = entry.text
			literals.put(text, literals.getOrDefault(text, 0) + 1)
		}
	}
}

private const val DEFAULT_DUPLICATION = 2
