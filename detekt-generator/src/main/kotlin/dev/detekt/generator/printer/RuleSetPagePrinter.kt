package dev.detekt.generator.printer

import dev.detekt.generator.collection.RuleSetPage
import dev.detekt.utils.markdown
import dev.detekt.utils.paragraph

object RuleSetPagePrinter : DocumentationPrinter<RuleSetPage> {

    override fun print(item: RuleSetPage): String =
        markdown {
            if (item.ruleSet.description.isNotEmpty()) {
                paragraph { item.ruleSet.description }
            } else {
                paragraph { "TODO: Specify description" }
            }
            markdown { RuleConfigurationPrinter.print(item.ruleSet.configuration) }

            item.rules.forEach {
                markdown { RulePrinter.print(it) }
            }
        }
}
