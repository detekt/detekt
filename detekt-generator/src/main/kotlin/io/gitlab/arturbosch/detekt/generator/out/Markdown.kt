@file:Suppress("detekt.TooManyFunctions")

package io.gitlab.arturbosch.detekt.generator.out

sealed class Markdown(open var content: String = "") {
    fun append(value: String) {
        content = if (content.isEmpty()) {
            value
        } else {
            "$content\n$value"
        }
    }
}

data class MarkdownContent(override var content: String = "") : Markdown()
data class MarkdownList(override var content: String = "") : Markdown()

inline fun markdown(content: MarkdownContent.() -> Unit): String {
    return MarkdownContent().let { markdown ->
        content(markdown)
        markdown.content
    }
}

inline fun MarkdownContent.markdown(markdown: () -> String) = append(markdown())
inline fun MarkdownContent.paragraph(content: () -> String) = append("${content()}\n")

inline fun MarkdownContent.bold(content: () -> String) = "**${content()}**"
inline fun MarkdownContent.crossOut(code: () -> String) = "~~${code()}~~"

inline fun MarkdownContent.h1(heading: () -> String) = append("# ${heading()}\n")
inline fun MarkdownContent.h2(heading: () -> String) = append("## ${heading()}\n")
inline fun MarkdownContent.h3(heading: () -> String) = append("### ${heading()}\n")
inline fun MarkdownContent.h4(heading: () -> String) = append("#### ${heading()}\n")

inline fun MarkdownContent.orderedList(sectionList: () -> List<String>) {
    for (i in sectionList().indices) {
        append("${i + 1}. ${sectionList()[i]}")
    }
}

inline fun MarkdownContent.referenceToHeading(reference: () -> String) =
        "[${reference()}](#${reference().replace(' ', '-').toLowerCase()})"

inline fun MarkdownContent.code(code: () -> String) = "``${code()}``"
inline fun MarkdownContent.codeBlock(code: () -> String) = "```kotlin\n${code()}\n```"

fun MarkdownContent.emptyLine() = append("")

inline fun MarkdownContent.list(listContent: MarkdownList.() -> Unit) {
    return MarkdownList().let { list ->
        listContent(list)
        if (list.content.isNotEmpty()) {
            append(list.content)
        }
    }
}

inline fun MarkdownList.item(item: () -> String) = append("* ${item()}\n")
inline fun MarkdownList.description(description: () -> String) = append("   ${description()}\n")
