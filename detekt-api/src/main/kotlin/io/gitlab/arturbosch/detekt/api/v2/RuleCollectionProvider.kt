package io.gitlab.arturbosch.detekt.api.v2

import io.gitlab.arturbosch.detekt.api.Config

/**
 * A rule collection provider, as the name states, is responsible for creating rule collection.
 *
 * When writing own rule set providers make sure to register them according the ServiceLoader documentation.
 * http://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html
 */
interface RuleCollectionProvider {

    val ruleSetId: String

    /**
     * This function must be implemented to provide custom rule sets.
     * Make sure to pass the configuration to each rule to allow rules
     * to be self configurable.
     */
    fun instance(config: Config): List<Rule>
}
