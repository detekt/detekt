package io.gitlab.arturbosch.detekt.api

import io.gitlab.arturbosch.detekt.api.internal.PathFilters

/**
 * A rule set provider, as the name states, is responsible for creating rule sets.
 *
 * When writing own rule set providers make sure to register them according the ServiceLoader documentation.
 * http://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html
 *
 * @author Artur Bosch
 */
interface RuleSetProvider {

    /**
     * Every rule set must be pre-configured with an ID to validate if this rule set
     * must be created for current analysis.
     */
    val ruleSetId: String

    /**
     * Can return a rule set if this specific rule set is not considered as ignore.
     *
     * Api notice: As the rule set id is not known before creating the rule set instance,
     * we must first create the rule set and then check if it is active.
     */
    fun buildRuleset(config: Config): RuleSet? {
        val subConfig = config.subConfig(ruleSetId)
        val active = subConfig.valueOrDefault("active", true)
        return if (active) {
            val includes = subConfig.valueOrNull<String>(Config.INCLUDES_KEY)?.trim()
            val excludes = subConfig.valueOrNull<String>(Config.EXCLUDES_KEY)?.trim()
            instance(subConfig).apply { pathFilters = PathFilters.of(includes, excludes) }
        } else {
            null
        }
    }

    /**
     * This function must be implemented to provide custom rule sets.
     * Make sure to pass the configuration to each rule to allow rules
     * to be self configurable.
     */
    fun instance(config: Config): RuleSet
}
