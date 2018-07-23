package io.gitlab.arturbosch.detekt.cli.out

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HtmlSnippetTest {

	@Test
	fun testGeneratingSimpleHtmlSnippet() {
		val snippet = htmlSnippet {
			h3 { "Hello World" }
			div("box") {
				text { "Test" }
				br()
			}
		}

		assertEquals("<h3>Hello World</h3>\n<div class=\"box\">\nTest\n<br />\n</div>", snippet)
	}

	@Test
	fun testGeneratingList() {
		val items = listOf("Apple", "Banana", "Orange")

		val snippet = htmlSnippet {
			list(items) {
				span("fruit") { it }
			}
		}

		assertEquals("<ul>\n<li>\n<span class=\"fruit\">\nApple\n</span>\n</li>\n<li>\n<span class=\"fruit\">\nBanana\n</span>\n</li>\n<li>\n<span class=\"fruit\">\nOrange\n</span>\n</li>\n</ul>", snippet)
	}
}
