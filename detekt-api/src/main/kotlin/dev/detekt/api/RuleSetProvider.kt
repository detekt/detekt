package dev.detekt.api

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
    val ruleSetId: RuleSet.Id

    /**
     * This function must be implemented to provide custom rule sets.
     */
    fun instance(): RuleSet
}
