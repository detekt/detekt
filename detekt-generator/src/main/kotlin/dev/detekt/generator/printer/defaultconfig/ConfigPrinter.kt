package dev.detekt.generator.printer.defaultconfig

import dev.detekt.generator.collection.RuleSetPage
import dev.detekt.generator.printer.DocumentationPrinter
import dev.detekt.utils.yaml

object ConfigPrinter : DocumentationPrinter<List<RuleSetPage>> {

    override fun print(item: List<RuleSetPage>): String =
        yaml {
            item.sortedBy { it.ruleSet.name }
                .forEach { printRuleSetPage(it) }
        }
}
