package io.gitlab.arturbosch.detekt.api

/**
 * Interface which is implemented by each Rule class to provide
 * utility functions to retrieve specific or generic properties
 * from the underlying detekt configuration file.
 *
 * Be aware that there are three config levels by default:
 * - the top level config layer specifies rule sets and detekt engine properties
 * - the rule set level specifies properties concerning the whole rule set and rules
 * - the rule level provides additional properties which are used to configure rules
 *
 * This interface operates on the rule set level as the rule set config is passed to each
 * rule in the #RuleSetProvider interface. This is due the fact that users create the
 * rule set and all rules upfront and letting them 'sub config' the rule set config would
 * be error-prone.
 */
interface ConfigAware : Config {

    /**
     * Id which is used to retrieve the sub config for the rule implementing this interface.
     */
    val ruleId: RuleId

    /**
     * Wrapped configuration of the ruleSet this rule is in.
     * Use #valueOrDefault function to retrieve properties specified for the rule
     * implementing this interface instead.
     * Only use this property directly if you need a specific rule set property.
     */
    val ruleSetConfig: Config

    private val ruleConfig: Config
        get() = ruleSetConfig.subConfig(ruleId)

    /**
     * Does this rule have auto correct specified in configuration?
     * For auto correction to work the rule set itself enable it.
     */
    val autoCorrect: Boolean
        get() = valueOrDefault("autoCorrect", false) &&
            ruleSetConfig.valueOrDefault("autoCorrect", true)

    /**
     * Is this rule specified as active in configuration?
     * If an rule is not specified in the underlying configuration, we assume it should not be run.
     */
    val active: Boolean get() = valueOrDefault("active", false)

    /**
     * If your rule supports to automatically correct the misbehaviour of underlying smell,
     * specify your code inside this method call, to allow the user of your rule to trigger auto correction
     * only when needed.
     */
    fun withAutoCorrect(block: () -> Unit) {
        if (autoCorrect) {
            block()
        }
    }

    override fun subConfig(key: String): Config =
        ruleConfig.subConfig(key)

    override fun <T : Any> valueOrDefault(key: String, default: T): T =
        ruleConfig.valueOrDefault(key, default)

    override fun <T : Any> valueOrNull(key: String): T? =
        ruleConfig.valueOrNull(key)
}
