package io.gitlab.arturbosch.detekt.api

/**
 * A rule set provider, as the name states, is responsible for creating rule sets.
 *
 * When writing own rule set providers make sure to register them according the ServiceLoader documentation.
 * http://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html
 */
interface RuleSetProvider {

    /**
     * Every rule set must be pre-configured with an ID to validate if this rule set
     * must be created for current analysis.
     */
    val ruleSetId: String

    /**
     * This function must be implemented to provide custom rule sets.
     * Make sure to pass the configuration to each rule to allow rules
     * to be self configurable.
     */
    fun instance(config: Config): RuleSet
}
