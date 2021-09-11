package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.collection.RuleSetPage

object DeprecatedPrinter : DocumentationPrinter<List<RuleSetPage>> {
    override fun print(item: List<RuleSetPage>): String {
        return """
            complexity>LongParameterList>threshold=Use 'functionThreshold' and 'constructorThreshold' instead
            empty-blocks>EmptyFunctionBlock>ignoreOverriddenFunctions=Use 'ignoreOverridden' instead
            naming>FunctionParameterNaming>ignoreOverriddenFunctions=Use 'ignoreOverridden' instead
            naming>MemberNameEqualsClassName>ignoreOverriddenFunction=Use 'ignoreOverridden' instead
            
        """.trimIndent()
    }
}
