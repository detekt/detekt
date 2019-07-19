package io.gitlab.arturbosch.detekt.api

/**
 * A context describes the storing and reporting mechanism of [Finding]'s inside a [Rule].
 * Additionally it handles suppression and aliases management.
 *
 * The detekt engine retrieves the findings after each KtFile visit and resets the context
 * before the next KtFile.
 */
interface Context {
    val findings: List<Finding>
    fun report(finding: Finding, aliases: Set<String> = emptySet())
    fun report(findings: List<Finding>, aliases: Set<String> = emptySet())
    fun clearFindings()
}

/**
 * Default [Context] implementation.
 */
open class DefaultContext : Context {

    /**
     * Returns a copy of violations for this rule.
     */
    override val findings: List<Finding>
        get() = _findings.toList()

    private var _findings: MutableList<Finding> = mutableListOf()

    /**
     * Reports a single code smell finding.
     *
     * Before adding a finding, it is checked if it is not suppressed
     * by @Suppress or @SuppressWarnings annotations.
     */
    override fun report(finding: Finding, aliases: Set<String>) {
        val ktElement = finding.entity.ktElement
        if (ktElement == null || !ktElement.isSuppressedBy(finding.id, aliases)) {
            _findings.add(finding)
        }
    }

    /**
     * Reports a list of code smell findings.
     *
     * Before adding a finding, it is checked if it is not suppressed
     * by @Suppress or @SuppressWarnings annotations.
     */
    override fun report(findings: List<Finding>, aliases: Set<String>) {
        findings.forEach { report(it, aliases) }
    }

    final override fun clearFindings() {
        _findings = mutableListOf()
    }
}
