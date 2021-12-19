package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.DefaultContext
import io.gitlab.arturbosch.detekt.api.internal.PathFilters
import io.gitlab.arturbosch.detekt.api.internal.createPathFilters
import io.gitlab.arturbosch.detekt.api.internal.isSuppressedBy
import java.util.Locale
import org.jetbrains.kotlin.psi.KtFile

/**
 * A rule defines how one specific code structure should look like. If code is found
 * which does not meet this structure, it is considered as harmful regarding maintainability
 * or readability.
 *
 * A rule is implemented using the visitor pattern and should be started using the visit(KtFile)
 * function. If calculations must be done before or after the visiting process, here are
 * two predefined (preVisit/postVisit) functions which can be overridden to setup/teardown additional data.
 */
abstract class IssueConfigurableRule(
    override val ruleSetConfig: Config = Config.empty,
    ruleContext: Context = DefaultContext()
) : BaseRule(ruleContext), ConfigAware {

    /**
     * The grade of severity of the code that violates the defined rule.
     */
    abstract val severity: Severity

    /**
     * A brief outline of the rule.
     */
    abstract val description: String

    /**
     * The amount of debt time that is to be assigned to infractions of the rule
     * provided that no configuration is provided for debt time in the rule's
     * configuration entry.
     */
    abstract val defaultDebt: Debt

    /**
     * A rule is motivated to point out a specific issue in the code base.
     */
    val issue: Issue by lazy { Issue(ruleId, severity, description, debt) }

    /**
     * The amount of debt time that is to be assigned to infractions of the rule.
     */
    private val debt: Debt
        get() = valueOrNull<Int>("debt")
            ?.let { Debt(mins = it) }
            ?: defaultDebt

    /**
     * List of rule ids which can optionally be used in suppress annotations to refer to this rule.
     */
    val aliases: Set<String> get() = valueOrDefault("aliases", defaultRuleIdAliases)

    /**
     * The default names which can be used instead of this [ruleId] to refer to this rule in suppression's.
     *
     * When overriding this property make sure to meet following structure for detekt-generator to pick
     * it up and generate documentation for aliases:
     *
     *      override val defaultRuleIdAliases = setOf("Name1", "Name2")
     */
    open val defaultRuleIdAliases: Set<String> = emptySet()

    internal val ruleSetId: RuleId? get() = ruleSetConfig.parentPath

    /**
     * Rules are aware of the paths they should run on via configuration properties.
     */
    open val filters: PathFilters? by lazy(LazyThreadSafetyMode.NONE) {
        createPathFilters()
    }

    override fun visitCondition(root: KtFile): Boolean =
        active && shouldRunOnGivenFile(root) && !root.isSuppressedBy(ruleId, aliases, ruleSetId)

    private fun shouldRunOnGivenFile(root: KtFile) =
        filters?.isIgnored(root)?.not() ?: true

    /**
     * Compute severity in the priority order:
     * - Severity of the rule
     * - Severity of the parent ruleset
     * - Default severity: warning
     */
    private fun computeSeverity(): SeverityLevel {
        val configValue: String = valueOrNull(Config.SEVERITY_KEY)
            ?: ruleSetConfig.valueOrDefault(Config.SEVERITY_KEY, "warning")
        return enumValueOf(configValue.toUpperCase(Locale.US))
    }

    /**
     * Simplified version of [Context.report] with rule defaults.
     */
    fun report(finding: Finding) {
        (finding as? CodeSmell)?.internalSeverity = computeSeverity()
        report(finding, aliases, ruleSetId)
    }

    /**
     * Simplified version of [Context.report] with rule defaults.
     */
    fun report(findings: List<Finding>) {
        findings.forEach {
            (it as? CodeSmell)?.internalSeverity = computeSeverity()
        }
        report(
            findings,
            aliases,
            ruleSetId
        )
    }
}
