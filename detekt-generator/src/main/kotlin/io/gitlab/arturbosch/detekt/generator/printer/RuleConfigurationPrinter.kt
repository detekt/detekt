package io.gitlab.arturbosch.detekt.generator.printer

import io.gitlab.arturbosch.detekt.generator.collection.Configuration
import io.gitlab.arturbosch.detekt.generator.out.bold
import io.gitlab.arturbosch.detekt.generator.out.code
import io.gitlab.arturbosch.detekt.generator.out.crossOut
import io.gitlab.arturbosch.detekt.generator.out.description
import io.gitlab.arturbosch.detekt.generator.out.h4
import io.gitlab.arturbosch.detekt.generator.out.item
import io.gitlab.arturbosch.detekt.generator.out.list
import io.gitlab.arturbosch.detekt.generator.out.markdown

internal object RuleConfigurationPrinter : DocumentationPrinter<List<Configuration>> {

    override fun print(item: List<Configuration>): String {
        if (item.isEmpty()) return ""
        return markdown {
            h4 { "Configuration options:" }
            list {
                item.forEach {
                    val defaultValues = it.defaultValue.getQuotedIfNecessary()
                    val defaultAndroidValues = it.defaultAndroidValue?.getQuotedIfNecessary()
                    val defaultString = if (defaultAndroidValues != null) {
                        "(default: ${code { defaultValues }}) (android default: ${code { defaultAndroidValues }})"
                    } else {
                        "(default: ${code { defaultValues }})"
                    }
                    if (it.isDeprecated()) {
                        item {
                            crossOut { code { it.name } } + " " + defaultString
                        }
                        description { "${bold { "Deprecated" }}: ${it.deprecated}" }
                    } else {
                        item {
                            code { it.name } + " " + defaultString
                        }
                    }
                    description { it.description }
                }
            }
        }
    }
}
