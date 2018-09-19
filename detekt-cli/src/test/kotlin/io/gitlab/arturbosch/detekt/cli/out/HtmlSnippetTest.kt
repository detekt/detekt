package io.gitlab.arturbosch.detekt.cli.out

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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

		assertThat(snippet).isEqualTo("<h3>Hello World</h3>\n<div class=\"box\">\nTest\n<br />\n</div>")
	}

	@Test
	fun testGeneratingList() {
		val items = listOf("Apple", "Banana", "Orange")

		val snippet = htmlSnippet {
			list(items) {
				span("fruit") { it }
			}
		}

		assertThat(snippet).isEqualTo("<ul>\n<li>\n<span class=\"fruit\">\nApple\n</span>\n</li>\n<li>\n<span class=\"fruit\">\nBanana\n</span>\n</li>\n<li>\n<span class=\"fruit\">\nOrange\n</span>\n</li>\n</ul>")
	}
}
