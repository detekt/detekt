package dev.detekt.generator.printer

import dev.detekt.generator.collection.Configuration
import dev.detekt.utils.bold
import dev.detekt.utils.code
import dev.detekt.utils.crossOut
import dev.detekt.utils.description
import dev.detekt.utils.h4
import dev.detekt.utils.item
import dev.detekt.utils.list
import dev.detekt.utils.markdown

internal object ConfigurationsPrinter : DocumentationPrinter<List<Configuration>> {

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
