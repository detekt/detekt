package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.validateIdentifier

/**
 * An issue represents a problem in the codebase.
 */
data class Issue(
    val id: String,
    val severity: Severity,
    val description: String,
    val debt: Debt
) {

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
    /**
     * Represents clean coding violations which may lead to maintainability issues.
     */
    CodeSmell,
    /**
     * Inspections in this category detect violations of code syntax styles.
     */
    Style,
    /**
     * Corresponds to issues that do not prevent the code from working,
     * but may nevertheless represent coding inefficiencies.
     */
    Warning,
    /**
     * Corresponds to coding mistakes which could lead to unwanted behavior.
     */
    Defect,
    /**
     * Represents code quality issues which only slightly impact the code quality.
     */
    Minor,
    /**
     * Issues in this category make the source code confusing and difficult to maintain.
     */
    Maintainability,
    /**
     * Places in the source code that can be exploited and possibly result in significant damage.
     */
    Security,
    /**
     * Places in the source code which degrade the performance of the application.
     */
    Performance
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
        val TWENTY_MINS: Debt = Debt(0, 0, 20)
        val TEN_MINS: Debt = Debt(0, 0, 10)
        val FIVE_MINS: Debt = Debt(0, 0, 5)
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
