package io.gitlab.arturbosch.detekt.api.internal

import io.gitlab.arturbosch.detekt.api.Context
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.api.RuleSetId

/**
 * Default [Context] implementation.
 */
internal class DefaultContext : Context {

    /**
     * Returns a copy of violations for this rule.
     */
    override val findings: List<Finding>
        get() = _findings.toList()

    private val _findings: MutableList<Finding> = mutableListOf()

    /**
     * Reports a single code smell finding.
     *
     * Before adding a finding, it is checked if it is not suppressed
     * by @Suppress or @SuppressWarnings annotations.
     */
    override fun report(finding: Finding, aliases: Set<String>, ruleSetId: RuleSetId?) {
        val ktElement = finding.entity.ktElement
        if (ktElement == null || !ktElement.isSuppressedBy(finding.id, aliases, ruleSetId)) {
            _findings.add(finding)
        }
    }

    override fun clearFindings() {
        _findings.clear()
    }
}
