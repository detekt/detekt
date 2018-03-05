package io.gitlab.arturbosch.detekt.api

/**
 * @author Artur Bosch
 * @author Marvin Ramin
 * @author schalkms
 */

/**
 * An issue represents a problem in the codebase.
 */
data class Issue(val id: String,
				 val severity: Severity,
				 val description: String,
				 val debt: Debt,
				 val aliases: Set<String> = setOf()) {

	init {
		validateIdentifier(id)
	}

	override fun toString(): String {
		return "Issue(id='$id', severity=$severity, debt=$debt)"
	}
}

/**
 * Rules can classified into different severity grades. Maintainer can choose
 * a grade which is most harmful to their projects.
 */
enum class Severity {
	CodeSmell, Style, Warning, Defect, Minor, Maintainability, Security, Performance

}

/**
 * Debt describes the estimated amount of work needed to fix a given issue.
 */
@Suppress("MagicNumber")
data class Debt(val days: Int = 0, val hours: Int = 0, val mins: Int = 0) {

	init {
		require(days >= 0 && hours >= 0 && mins >= 0)
		require(!(days == 0 && hours == 0 && mins == 0))
	}

	companion object {
		val TWENTY_MINS = Debt(0, 0, 20)
		val TEN_MINS = Debt(0, 0, 10)
		val FIVE_MINS = Debt(0, 0, 5)
	}

	override fun toString(): String {
		return with(StringBuilder()) {
			if (days > 0) append("${days}d ")
			if (hours > 0) append("${hours}h ")
			if (mins > 0) append("${mins}min")
			toString()
		}.trim()
	}
}
