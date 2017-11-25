package io.gitlab.arturbosch.detekt.generator.out

/**
 * @author Marvin Ramin
 */
class Markdown(var content: String = "") {
	fun append(value: String) {
		content = if (content.isEmpty()) {
			value
		} else {
			"$content\n$value"
		}
	}
}

class MarkdownList(var content: String = "") {
	fun append(value: String) {
		content = if (content.isEmpty()) {
			value
		} else {
			"$content\n$value"
		}
	}
}

inline fun markdown(content: Markdown.() -> Unit): String {
	val markdown = Markdown()
	content(markdown)
	return markdown.content
}
inline fun Markdown.markdown(markdown: () -> String) = append(markdown())
inline fun Markdown.paragraph(content: () -> String) = append("${content()}\n")

inline fun Markdown.h1(heading: () -> String) = append("# ${heading()}\n")
inline fun Markdown.h2(heading: () -> String) = append("## ${heading()}\n")
inline fun Markdown.h3(heading: () -> String) = append("### ${heading()}\n")
inline fun Markdown.h4(heading: () -> String) = append("#### ${heading()}\n")

inline fun Markdown.code(code: () -> String) = "`${code()}`"
fun Markdown.emptyLine() = append("")

inline fun Markdown.list(listContent: MarkdownList.() -> Unit) {
	val list = MarkdownList()
	listContent(list)
	if (list.content.isNotEmpty()) {
		append(list.content)
	}
}
inline fun MarkdownList.item(item: () -> String) = append("* ${item()}\n")
inline fun MarkdownList.description(description: () -> String) = append("   ${description()}\n")


