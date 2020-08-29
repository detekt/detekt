package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.isSuppressedBy
import org.jetbrains.kotlin.psi.KtFile

/**
 * A context describes the storing and reporting mechanism of [Finding]'s inside a [Rule].
 * Additionally it handles suppression and aliases management.
 *
 * The detekt engine retrieves the findings after each KtFile visit and resets the context
 * before the next KtFile.
 */
interface Context {

    val findings: List<Finding>

    /**
     * Reports a single new violation.
     * By contract the implementation can check if
     * this finding is already suppressed and should not get reported.
     * An alias set can be given to additionally check if an alias was used when suppressing.
     * Additionally suppression by rule set id is supported.
     */
    fun report(finding: Finding, aliases: Set<String> = emptySet(), ruleSetId: RuleSetId? = null) {
        report(finding, aliases, null)
    }

    /**
     * Same as [report] but reports a list of findings.
     */
    fun report(findings: List<Finding>, aliases: Set<String> = emptySet(), ruleSetId: RuleSetId? = null) {
        report(findings, aliases, null)
    }

    /**
     * Clears previous findings.
     * Normally this is done on every new [KtFile] analyzed and should be called by clients.
     */
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
    override fun report(finding: Finding, aliases: Set<String>, ruleSetId: RuleSetId?) {
        val ktElement = finding.entity.ktElement
        if (ktElement == null || !ktElement.isSuppressedBy(finding.id, aliases, ruleSetId)) {
            _findings.add(finding)
        }
    }

    /**
     * Reports a list of code smell findings.
     *
     * Before adding a finding, it is checked if it is not suppressed
     * by @Suppress or @SuppressWarnings annotations.
     */
    override fun report(findings: List<Finding>, aliases: Set<String>, ruleSetId: RuleSetId?) {
        findings.forEach { report(it, aliases, ruleSetId) }
    }

    final override fun clearFindings() {
        _findings = mutableListOf()
    }
}
