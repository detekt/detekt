package io.gitlab.arturbosch.detekt.generator.out

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class MarkdownContentSpec : Spek({

	given("some markdown headings") {

		it("prints the correct content") {
			val expected = "# H1\n\n## H2\n\n### H3\n\n#### H4\n"
			val actual = markdown {
				h1 { "H1" }
				h2 { "H2" }
				h3 { "H3" }
				h4 { "H4" }
			}
			assertThat(expected).isEqualTo(actual)
		}
	}

	given("a markdown paragraph") {

		it("prints the correct content") {
			val expected = "asdf\n"
			val actual = markdown {
				paragraph { "asdf" }
			}
			assertThat(expected).isEqualTo(actual)
		}
	}

	given("a markdown ordered list") {

		it("prints the correct content") {
			val expected = "1. first\n2. second"
			val actual = markdown {
				orderedList { listOf("first", "second") }
			}
			assertThat(expected).isEqualTo(actual)
		}
	}

	given("a markdown reference to a heading") {

		it("prints the correct content") {
			val expected = "[Some text](#Some-text)\n"
			val actual = markdown {
				paragraph { referenceToHeading { "Some text" } }
			}
			assertThat(expected).isEqualTo(actual)
		}
	}

	given("markdown source code snippets") {

		it("prints the correct single-line code") {
			val expected = "`Hello World`\n"
			val actual = markdown {
				paragraph { code { "Hello World" } }
			}
			assertThat(expected).isEqualTo(actual)
		}

		it("prints the correct multi-line code") {
			val expected = "```kotlin\nprintln()\n```\n"
			val actual = markdown {
				paragraph { codeBlock { "println()" } }
			}
			assertThat(expected).isEqualTo(actual)
		}
	}
})
