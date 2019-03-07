package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.absolutePath
import io.gitlab.arturbosch.detekt.api.internal.pathMatcher
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.PathMatcher
import java.nio.file.Paths

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

    /**
     * When specified this rule only runs on [KtFile]'s with paths matching any inclusion pattern.
     * @return path matchers or null which means for every [KtFile] this rule must run
     */
    open val includes: Set<PathMatcher>?
        get() = pathMatchers(Config.INCLUDES_KEY)

    /**
     * When specified this rule will not run on [KtFile]'s having a path matching any exclusion pattern.
     * @return path matchers or null which means no [KtFile]'s get excluded except inclusion patterns are defined
     */
    open val excludes: Set<PathMatcher>?
        get() = pathMatchers(Config.EXCLUDES_KEY)

    private fun pathMatchers(key: String): Set<PathMatcher>? {
        val raw = valueOrNull<String>(key)?.trim()
        return if (raw == null) {
            null
        } else {
            SplitPattern(raw).mapAll { pathMatcher(it) }.toSet()
        }
    }

    override fun visitCondition(root: KtFile): Boolean =
        active &&
            shouldRunOnFile(root) &&
            !root.isSuppressedBy(ruleId, aliases)

    private fun shouldRunOnFile(file: KtFile): Boolean {
        val excludeMatchers = excludes
        val includeMatchers = includes

        if (excludeMatchers == null && includeMatchers == null) {
            return true // need no checking
        }

        val rawPath = file.absolutePath()
            ?: throw IllegalStateException("KtFile '${file.name}' expected to have an absolute path.")
        val path = Paths.get(rawPath)

        fun isIncluded() = includeMatchers?.any { it.matches(path) }
        fun isExcluded() = excludeMatchers?.any { it.matches(path) }

        return if (isExcluded() == true) {
            isIncluded() ?: false
        } else {
            isIncluded() ?: true
        }
    }

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
