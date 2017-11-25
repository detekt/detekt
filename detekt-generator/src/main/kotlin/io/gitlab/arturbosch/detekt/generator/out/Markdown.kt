package io.gitlab.arturbosch.detekt.generator.out

/**
 * @author Marvin Ramin
 */
sealed class MD(open var content: String = "") {
	fun append(value: String) {
		content = if (content.isEmpty()) {
			value
		} else {
			"$content\n$value"
		}
	}
}

data class Markdown(override var content: String = "") : MD()
data class MarkdownList(override var content: String = "") : MD()

inline fun markdown(content: Markdown.() -> Unit): String {
	return Markdown().let { markdown ->
		content(markdown)
		markdown.content
	}
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
	return MarkdownList().let { list ->
		listContent(list)
		if (list.content.isNotEmpty()) {
			append(list.content)
		}
	}
}

inline fun MarkdownList.item(item: () -> String) = append("* ${item()}\n")
inline fun MarkdownList.description(description: () -> String) = append("   ${description()}\n")


