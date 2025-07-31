@file:Suppress("detekt.TooManyFunctions")

package dev.detekt.utils

sealed class Markdown(open var content: String = "") {
    fun append(value: String) {
        if (value.isEmpty()) return
        content = if (content.isEmpty()) {
            value
        } else {
            "$content\n$value"
        }
    }
}

data class MarkdownContent(override var content: String = "") : Markdown()
data class MarkdownList(override var content: String = "") : Markdown()

inline fun markdown(content: MarkdownContent.() -> Unit): String =
    MarkdownContent().let { markdown ->
        content(markdown)
        markdown.content
    }

inline fun MarkdownContent.markdown(markdown: () -> String): Unit = append(markdown())
inline fun MarkdownContent.paragraph(content: () -> String): Unit = append("${content()}\n")

inline fun MarkdownContent.bold(content: () -> String) = "**${content()}**"
inline fun MarkdownContent.crossOut(code: () -> String) = "~~${code()}~~"

inline fun MarkdownContent.h1(heading: () -> String): Unit = append("# ${heading()}\n")
inline fun MarkdownContent.h2(heading: () -> String): Unit = append("## ${heading()}\n")
inline fun MarkdownContent.h3(heading: () -> String): Unit = append("### ${heading()}\n")
inline fun MarkdownContent.h4(heading: () -> String): Unit = append("#### ${heading()}\n")

inline fun MarkdownContent.orderedList(sectionList: () -> List<String>) {
    for (i in sectionList().indices) {
        append("${i + 1}. ${sectionList()[i]}")
    }
}

// Note: Use double-backticks here to be able to render code that itself contains backticks.
inline fun MarkdownContent.code(code: () -> String) = "``${code()}``"

inline fun MarkdownContent.codeBlock(syntax: String = "kotlin", code: () -> String) = "```$syntax\n${code()}\n```"

fun MarkdownContent.emptyLine(): Unit = append("")

inline fun MarkdownContent.list(listContent: MarkdownList.() -> Unit): Unit =
    MarkdownList().let { list ->
        listContent(list)
        if (list.content.isNotEmpty()) {
            append(list.content)
        }
    }

inline fun MarkdownList.item(item: () -> String): Unit = append("* ${item()}\n")
inline fun MarkdownList.description(description: () -> String): Unit = append("  ${description()}\n")
