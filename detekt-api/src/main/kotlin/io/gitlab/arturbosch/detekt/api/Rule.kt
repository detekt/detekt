package io.gitlab.arturbosch.detekt.api

import org.jetbrains.kotlin.psi.KtFile

/**
 * A rule defines how one specific code structure should look like. If code is found
 * which does not meet this structure, it is considered as harmful regarding maintainability
 * or readability.
 *
 * A rule is implemented using the visitor pattern and should be started using the visit(KtFile)
 * function. If calculations must be done before or after the visiting process, here are
 * two predefined (preVisit/postVisit) functions which can be overridden to setup/teardown additional data.
 *
 * @author Artur Bosch
 * @author Marvin Ramin
 */
abstract class Rule(
    override val ruleSetConfig: Config = Config.empty,
    ruleContext: Context = DefaultContext()) :
        BaseRule(ruleContext), ConfigAware {

    /**
     * A rule is motivated to point out a specific issue in the code base.
     */
    abstract val issue: Issue

    /**
     * An id this rule is identified with.
     * Conventionally the rule id is derived from the issue id as these two classes have a coexistence.
     */
    final override val ruleId: RuleId get() = issue.id

    /**
     * List of rule ids which can optionally be used in suppress annotations to refer to this rule.
     */
    val aliases: Set<String> get() = valueOrDefault("aliases", defaultRuleIdAliases)

    /**
     * The default names which can be used instead of this #ruleId to refer to this rule in suppression's.
     *
     * When overriding this property make sure to meet following structure for detekt-generator to pick
     * it up and generate documentation for aliases:
     *
     * 		override val defaultRuleIdAliases = setOf("Name1", "Name2")
     */
    open val defaultRuleIdAliases: Set<String> = emptySet()

    override fun visitCondition(root: KtFile): Boolean = active && !root.isSuppressedBy(ruleId, aliases)

    /**
     * Simplified version of [Context.report] with aliases retrieval from the config.
     */
    fun report(finding: Finding) {
        report(finding, aliases)
    }

    /**
     * Simplified version of [Context.report] with aliases retrieval from the config.
     */
    fun report(findings: List<Finding>) {
        report(findings, aliases)
    }
}
