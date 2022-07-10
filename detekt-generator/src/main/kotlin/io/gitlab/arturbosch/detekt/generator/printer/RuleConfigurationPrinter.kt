package io.gitlab.arturbosch.detekt.generator.printer

import io.github.detekt.utils.bold
import io.github.detekt.utils.code
import io.github.detekt.utils.crossOut
import io.github.detekt.utils.description
import io.github.detekt.utils.h4
import io.github.detekt.utils.item
import io.github.detekt.utils.list
import io.github.detekt.utils.markdown
import io.gitlab.arturbosch.detekt.generator.collection.Configuration

internal object RuleConfigurationPrinter : DocumentationPrinter<List<Configuration>> {

    override fun print(item: List<Configuration>): String {
        if (item.isEmpty()) return ""
        return markdown {
            h4 { "Configuration options:" }
            list {
                item.forEach {
                    val defaultValues = it.defaultValue.printAsMarkdownCode()
                    val defaultAndroidValues = it.defaultAndroidValue?.printAsMarkdownCode()
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
