package io.gitlab.arturbosch.detekt.api.dsl

import io.gitlab.arturbosch.detekt.api.internal.BaseRule

// We will need one class and one extension function for each rule.
// And this should be defined in the file of each rule

@ConfigDsl
class UndocumentedPublicClassBuilder(
    var searchInNestedClass: Boolean = true,
    var searchInInnerClass: Boolean = true,
    var searchInInnerObject: Boolean = true,
    var searchInInnerInterface: Boolean = true,
) : RuleBuilder<BaseRule>, BaseRuleBuilder by BaseRuleBuilderImpl() {

    override fun buildRule(): BaseRule {
        TODO("Not yet implemented")
    }
}

@ConfigDsl
fun RuleSetBuilder.undocumentedPublicClass(init: UndocumentedPublicClassBuilder.() -> Unit) {
    addRule(UndocumentedPublicClassBuilder().apply(init))
}
