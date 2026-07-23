package dev.detekt.generator.printer

import dev.detekt.generator.collection.Active
import dev.detekt.generator.collection.Rule
import dev.detekt.utils.MarkdownContent
import dev.detekt.utils.bold
import dev.detekt.utils.codeBlock
import dev.detekt.utils.crossOut
import dev.detekt.utils.h3
import dev.detekt.utils.h4
import dev.detekt.utils.markdown
import dev.detekt.utils.paragraph

internal object RulePrinter : DocumentationPrinter<Rule> {

    private val javaAutolinkRegex = Regex("""\[((?:java|javax)\.[\w.]+)](?!\()""")

    override fun print(item: Rule): String =
        markdown {
            if (item.isDeprecated()) {
                h3 { crossOut { item.name } }
                paragraph { item.deprecationMessage.orEmpty() }
            } else {
                h3 { item.name }
            }

            if (item.description.isNotEmpty()) {
                paragraph { item.description.withJavadocLinks() }
            } else {
                paragraph { "TODO: Specify description" }
            }

            paragraph {
                "${bold { "Active by default" }}: ${if (item.defaultActivationStatus.active) "Yes" else "No"}" +
                    ((item.defaultActivationStatus as? Active)?.let { " - Since v${it.since}" }.orEmpty())
            }

            if (item.requiresFullAnalysis) {
                paragraph {
                    bold { "Requires Type Resolution" }
                }
            }

            if (item.aliases.isNotEmpty()) {
                paragraph {
                    "${bold { "Aliases" }}: ${item.aliases.joinToString(", ")}"
                }
            }

            markdown { ConfigurationsPrinter.print(item.configurations) }

            printRuleCodeExamples(item)
        }

    // KDoc autolinks to Java types (e.g. `[java.util.Locale]`) resolve nowhere in
    // the rendered markdown — without a target they reach the website as literal
    // bracketed text (#9533). Rewrite them as links to the Java SE API docs, using
    // the module-less javase/8 URL scheme the docs already use elsewhere. Segment
    // heuristic: lowercase segments are packages; the first capitalized segment
    // starts the class chain; following CamelCase segments are nested classes;
    // anything after that (ALL_CAPS constants, lowercase members) becomes the
    // anchor. A pure package reference has nothing stable to link to and is left
    // untouched, as are regular `[text](url)` markdown links (lookahead).
    private fun String.withJavadocLinks(): String =
        javaAutolinkRegex.replace(this) { match ->
            val reference = match.groupValues[1]
            val segments = reference.split('.')
            val classIndex = segments.indexOfFirst { it.first().isUpperCase() }
            if (classIndex == -1) {
                match.value
            } else {
                val packagePath = segments.take(classIndex).joinToString("/")
                val classChain = mutableListOf(segments[classIndex])
                var next = classIndex + 1
                while (next < segments.size &&
                    segments[next].first().isUpperCase() &&
                    segments[next] != segments[next].uppercase()
                ) {
                    classChain += segments[next]
                    next++
                }
                val anchor = segments.drop(next).joinToString(".")
                    .let { if (it.isEmpty()) "" else "#$it" }
                val url = "https://docs.oracle.com/javase/8/docs/api/" +
                    "$packagePath/${classChain.joinToString(".")}.html$anchor"
                "[`$reference`]($url)"
            }
        }

    private fun MarkdownContent.printRuleCodeExamples(rule: Rule) {
        if (rule.nonCompliantCodeExample.isNotEmpty()) {
            h4 { "Noncompliant Code:" }
            paragraph { codeBlock { rule.nonCompliantCodeExample } }
        }

        if (rule.compliantCodeExample.isNotEmpty()) {
            h4 { "Compliant Code:" }
            paragraph { codeBlock { rule.compliantCodeExample } }
        }
    }
}
