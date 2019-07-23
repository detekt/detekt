package io.gitlab.arturbosch.detekt.generator.collection

import org.jetbrains.kotlin.psi.KtFile

class RuleCollector : Collector<Rule> {
    override val items = mutableListOf<Rule>()

    override fun visit(file: KtFile) {
        val visitor = RuleVisitor()
        file.accept(visitor)

        if (visitor.containsRule) {
            items.add(visitor.getRule())
        }
    }
}
