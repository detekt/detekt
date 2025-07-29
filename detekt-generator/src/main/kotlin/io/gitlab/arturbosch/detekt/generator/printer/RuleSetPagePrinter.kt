package io.gitlab.arturbosch.detekt.generator.printer

import dev.detekt.utils.markdown
import dev.detekt.utils.paragraph
import io.gitlab.arturbosch.detekt.generator.collection.RuleSetPage

object RuleSetPagePrinter : DocumentationPrinter<RuleSetPage> {

    override fun print(item: RuleSetPage): String =
        markdown {
            if (item.ruleSet.description.isNotEmpty()) {
                paragraph { item.ruleSet.description }
            } else {
                paragraph { "TODO: Specify description" }
            }
            item.rules.forEach {
                markdown { RulePrinter.print(it) }
            }
        }
}
