package io.gitlab.arturbosch.detekt.cli.out

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it

class HtmlSnippetTest : Spek({

	it("testGeneratingSimpleHtmlSnippet") {
		val snippet = htmlSnippet {
			h3 { "Hello World" }
			div("box") {
				text { "Test" }
				br()
			}
		}

		assertThat(snippet).isEqualTo("<h3>Hello World</h3>\n<div class=\"box\">\nTest\n<br />\n</div>")
	}

	it("testGeneratingList") {
		val items = listOf("Apple", "Banana", "Orange")

		val snippet = htmlSnippet {
			list(items) {
				span("fruit") { it }
			}
		}

		assertThat(snippet).isEqualTo("<ul>\n<li>\n<span class=\"fruit\">\nApple\n</span>\n</li>\n<li>\n<span class=\"fruit\">\nBanana\n</span>\n</li>\n<li>\n<span class=\"fruit\">\nOrange\n</span>\n</li>\n</ul>")
	}
})
