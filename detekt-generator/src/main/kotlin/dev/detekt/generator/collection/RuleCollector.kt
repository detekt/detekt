package dev.detekt.generator.collection

import org.jetbrains.kotlin.psi.KtFile

class RuleCollector(private val textReplacements: Map<String, String>) : Collector<Rule> {
    override val items = mutableListOf<Rule>()

    override fun visit(file: KtFile) {
        val visitor = RuleVisitor(textReplacements)
        file.accept(visitor)

        if (visitor.containsRule) {
            items.add(visitor.getRule())
        }
    }
}
