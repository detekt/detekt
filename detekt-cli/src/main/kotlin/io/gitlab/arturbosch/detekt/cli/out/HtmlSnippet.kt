package io.gitlab.arturbosch.detekt.cli.out

/**
 * A very simple DSL for generating HTML.
 */
internal class HTMLSnippet {

	private val lines = mutableListOf<String>()

	fun h3(body: () -> String) {
		lines.add("<h3>${body()}</h3>")
	}

	fun div(cssClass: String, body: HTMLSnippet.() -> Unit) {
		lines.add("<div class=\"$cssClass\">")

		body()

		lines.add("</div>")
	}

	fun text(body: () -> String) {
		lines.add(body())
	}

	fun br() {
		lines.add("<br />")
	}

	fun span(cssClass: String, text: () -> String) {
		lines.add("<span class=\"$cssClass\">")
		lines.add(text())
		lines.add("</span>")
	}

	fun <T> list(collection: Collection<T>, body: HTMLSnippet.(T) -> Unit) {
		lines.add("<ul>")

		collection.forEach {
			lines.add("<li>")
			body(it)
			lines.add("</li>")
		}

		lines.add("</ul>")
	}

	override fun toString(): String {
		return lines.joinToString("\n")
	}
}

internal fun htmlSnippet(init: HTMLSnippet.() -> Unit): String {
	val snippet = HTMLSnippet()
	snippet.init()
	return snippet.toString()
}
