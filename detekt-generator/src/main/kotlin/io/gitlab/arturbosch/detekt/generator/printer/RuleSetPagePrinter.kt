package io.gitlab.arturbosch.detekt.generator.printer

import io.github.detekt.tooling.out.markdown
import io.github.detekt.tooling.out.paragraph
import io.gitlab.arturbosch.detekt.generator.collection.RuleSetPage

object RuleSetPagePrinter : DocumentationPrinter<RuleSetPage> {

    override fun print(item: RuleSetPage): String {
        return markdown {
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
}
