package io.gitlab.arturbosch.detekt.api.dsl

import io.gitlab.arturbosch.detekt.api.internal.BaseRule

@DslMarker
annotation class ConfigDsl

@ConfigDsl
fun rules(init: RulesBuilder.() -> Unit): List<RuleBuilder<BaseRule>> {
    return RulesBuilder().apply(init).list
        .flatMap { ruleSet -> ruleSet.rules }
}

@ConfigDsl
class RulesBuilder {
    internal val list: MutableList<RuleSet> = mutableListOf()

    fun ruleSet(name: String, builder: RuleSetBuilder.() -> Unit) {
        list.add(RuleSetBuilder().apply(builder).build(name))
    }
}

@ConfigDsl
class RuleSetBuilder(
    var active: Boolean = false,
    var include: MutableList<String> = mutableListOf(),
    var exclude: MutableList<String> = mutableListOf(),
    private val rules: MutableList<RuleBuilder<BaseRule>> = mutableListOf(),
) {
    internal fun build(name: String): RuleSet {
        return RuleSet(name, active, include, exclude, rules)
    }

    fun addRule(ruleBuilder: RuleBuilder<BaseRule>) {
        rules.add(ruleBuilder)
    }
}

@ConfigDsl
internal data class RuleSet(
    val name: String,
    val active: Boolean = false,
    val include: MutableList<String> = mutableListOf(),
    val exclude: MutableList<String> = mutableListOf(),
    val rules: MutableList<RuleBuilder<BaseRule>> = mutableListOf(),
)

@ConfigDsl
interface RuleBuilder<out T : BaseRule> : BaseRuleBuilder {
    fun buildRule(): T

    fun build(): RuleConfiguration<T> {
        return build { buildRule() }
    }
}

data class RuleConfiguration<out T>(
    val include: MutableList<String>,
    val exclude: MutableList<String>,
    val aliases: MutableList<String>,
    val factory: () -> T
)

@ConfigDsl
interface BaseRuleBuilder {
    var active: Boolean
    var include: MutableList<String>
    var exclude: MutableList<String>
    var aliases: MutableList<String>

    fun <T : BaseRule> build(factory: () -> T): RuleConfiguration<T>
}

@ConfigDsl
class BaseRuleBuilderImpl(
    override var active: Boolean = false,
    override var include: MutableList<String> = mutableListOf(),
    override var exclude: MutableList<String> = mutableListOf(),
    override var aliases: MutableList<String> = mutableListOf(),
) : BaseRuleBuilder {
    override fun <T : BaseRule> build(factory: () -> T): RuleConfiguration<T> {
        return RuleConfiguration(include, exclude, aliases, factory)
    }
}
