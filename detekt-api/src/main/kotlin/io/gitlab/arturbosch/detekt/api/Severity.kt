package io.gitlab.arturbosch.detekt.api

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
